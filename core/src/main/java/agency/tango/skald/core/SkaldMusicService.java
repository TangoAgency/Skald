package agency.tango.skald.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agency.tango.skald.core.listeners.OnErrorListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.listeners.OnPreparedListener;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import io.reactivex.Single;

public class SkaldMusicService {
  public static final String INTENT_ACTION = "auth_action";
  public static final String EXTRA_AUTH_DATA = "auth_data";

  private final List<OnPreparedListener> onPreparedListeners = new ArrayList<>();
  private final List<OnErrorListener> onErrorListeners = new ArrayList<>();
  private final List<Provider> providers = new ArrayList<>();
  private final Context context;
  private final PlayerCache playerCache;

  private SkaldTrack currentTrack;
  private SkaldPlaylist currentPlaylist;

  public SkaldMusicService(Context context, final Provider... providers) {
    this.providers.addAll(Arrays.asList(providers));
    this.context = context.getApplicationContext();
    this.playerCache = new PlayerCache();

    BroadcastReceiver messageReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        SkaldAuthData skaldAuthData = intent.getExtras().getParcelable(EXTRA_AUTH_DATA);
        getSkaldAuthStore().save(context, skaldAuthData);
      }
    };

    LocalBroadcastManager
        .getInstance(context.getApplicationContext())
        .registerReceiver(messageReceiver, new IntentFilter(INTENT_ACTION));
  }

  public void setSource(SkaldTrack skaldTrack) {
    currentTrack = skaldTrack;
  }

  public void setSource(SkaldPlaylist skaldPlaylist) {
    currentPlaylist = skaldPlaylist;
  }

  public void prepare() throws AuthException {
    getPlayer(currentTrack);
  }

  public void prepareAsync() {

  }

  public void play() {
    if (currentPlaylist != null) {
      //getPlayer(currentPlaylist).play(currentPlaylist);
    } else if (currentTrack != null) {
      try {
        getPlayer(currentTrack).play(currentTrack);
      } catch (AuthException authException) {
        notifyError();
      }
    }
  }

  public void pause() {
    try {
      getPlayer(currentTrack).pause();
    } catch (AuthException authException) {
      notifyError();
    }
  }

  public void resume() {
    try {
      getPlayer(currentTrack).resume();
    } catch (AuthException authException) {
      notifyError();
    }
  }

  public void stop() {
    try {
      getPlayer(currentTrack).stop();
    } catch (AuthException authException) {
      notifyError();
    }
  }

  public void release() {
    try {
      getPlayer(currentTrack).release();
    } catch (AuthException authException) {
      notifyError();
    }

  }

  private void notifyError() {
    for (OnErrorListener onErrorListener : onErrorListeners) {
      onErrorListener.onError();
    }
  }

  public void addOnErrorListener(OnErrorListener onErrorListener) {
    onErrorListeners.add(onErrorListener);
  }

  public void removeOnErrorListener(OnErrorListener onErrorListener) {
    onErrorListeners.remove(onErrorListener);
  }

  public void addOnPreparedListener(OnPreparedListener onPreparedListener) {
    onPreparedListeners.add(onPreparedListener);
  }

  public void removeOnPreparedListener(OnPreparedListener onPreparedListener) {
    onPreparedListeners.remove(onPreparedListener);
  }

  public void addOnPlaybackListener(OnPlaybackListener onPlaybackListener) {
    try {
      getPlayer(currentTrack).addOnPlaybackListener(onPlaybackListener);
    } catch (AuthException e) {
      notifyError();
    }
  }

  public void removeOnPlaybackListener(OnPlaybackListener onPlaybackListener) {
    try {
      getPlayer(currentTrack).removeOnPlaybackListener(onPlaybackListener);
    } catch (AuthException e) {
      notifyError();
    }
  }

  public Single<List<SkaldTrack>> searchTrack(String query) throws AuthException {
    return getSearchService().searchForTracks(query);
  }

  public Single<List<SkaldPlaylist>> searchPlayList(String query) throws AuthException {
    return getSearchService().searchForPlaylists(query);
  }

  private Player getPlayer(SkaldTrack skaldTrack) throws AuthException {
    for (Provider provider : providers) {
      if (provider.canHandle(skaldTrack)) {
        return playerCache.getForProvider(provider);
      }
    }
    throw new IllegalStateException();
  }

  private SkaldAuthStore getSkaldAuthStore() {
    return providers.get(0)
        .getSkaldAuthStoreFactory()
        .getSkaldAuthStore();
  }

  private SearchService getSearchService() throws AuthException {
    return null;
  }

  private class PlayerCache {
    private Map<String, Player> playerMap = new HashMap<>();

    private Player getForProvider(Provider provider) throws AuthException {
      if (playerMap.containsKey(provider.getProviderName())) {
        return playerMap.get(provider.getProviderName());
      } else {
        Player player = provider.getPlayerFactory().getPlayer();
        addPlayerReadyListener(player);
        playerMap.put(provider.getProviderName(), player);
        return player;
      }
    }

    private void addPlayerReadyListener(Player player) {
      player.addPlayerReadyListener(new OnPlayerReadyListener() {
        @Override
        public void onPlayerReady(Player player) {
          for (OnPreparedListener onPreparedListener : onPreparedListeners) {
            onPreparedListener.onPrepared(SkaldMusicService.this);
          }
        }
      });
    }
  }
}