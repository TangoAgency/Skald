package agency.tango.skald.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import agency.tango.skald.core.listeners.OnErrorListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.listeners.OnPreparedListener;
import agency.tango.skald.core.listeners.onPlaybackListener;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.core.models.TrackMetadata;
import io.reactivex.Single;

public class SkaldMusicService {
  public static final String INTENT_ACTION = "auth_action";
  public static final String EXTRA_AUTH_DATA = "auth_data";

  private final List<OnPreparedListener> onPreparedListeners = new ArrayList<>();
  private final List<OnErrorListener> onErrorListeners = new ArrayList<>();
  private final List<onPlaybackListener> onPlaybackListeners = new ArrayList<>();
  private final List<Provider> providers = new ArrayList<>();
  private final Context context;

  private Player player;
  private SkaldTrack currentTrack;
  private SkaldPlaylist currentPlaylist;

  public SkaldMusicService(Context context, final Provider... providers) {
    this.providers.addAll(Arrays.asList(providers));
    this.context = context.getApplicationContext();

    BroadcastReceiver messageReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        SkaldAuthData skaldAuthData = intent.getExtras().getParcelable(EXTRA_AUTH_DATA);
        getSkaldAuthStore().save(context, skaldAuthData);
        if (player == null) {
          player = getPlayer(skaldAuthData);
        }
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
    player = getPlayer(getSkaldAuthStore().restore(context, providers.get(0)));
  }

  public void prepareAsync() {

  }

  public void playTrack() {
    player.play(currentTrack);
  }

  public void playPlaylist() {
    player.play(currentPlaylist);
  }

  public void pause() {
    player.pause();
  }

  public void resume() {
    player.resume();
  }

  public void stop() {

  }

  public void release() {
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

  public void addOnPlabackListener(onPlaybackListener onPlaybackListener) {
    onPlaybackListeners.add(onPlaybackListener);
  }

  public void removeOnPlabackListener(onPlaybackListener onPlaybackListener) {
    onPlaybackListeners.remove(onPlaybackListener);
  }

  public Single<List<SkaldTrack>> searchTrack(String query) throws AuthException {
    return getSearchService().searchForTracks(query);
  }

  public Single<List<SkaldPlaylist>> searchPlayList(String query) throws AuthException {
    return getSearchService().searchForPlaylists(query);
  }

  private Player getPlayer(SkaldAuthData skaldAuthData) {
    Player player = providers.get(0)
        .getPlayerFactory()
        .getPlayer(skaldAuthData);

    player.addPlayerReadyListener(new OnPlayerReadyListener() {
      @Override
      public void onPlayerReady(Player player) {
        for (OnPreparedListener onPreparedListener : onPreparedListeners) {
          onPreparedListener.onPrepared(SkaldMusicService.this);
        }
      }
    });

    player.addOnPlabackListener(new onPlaybackListener() {
      @Override
      public void onPlaybackEvent(TrackMetadata trackMetadata) {
        for (onPlaybackListener onPlaybackListener : onPlaybackListeners) {
          onPlaybackListener.onPlaybackEvent(trackMetadata);
        }
      }
    });

    return player;
  }

  private SkaldAuthStore getSkaldAuthStore() {
    return providers.get(0)
        .getSkaldAuthStoreFactory()
        .getSkaldAuthStore();
  }

  private SearchService getSearchService() throws AuthException {
    return providers.get(0)
        .getSearchServiceFactory()
        .getSearchService(getSkaldAuthStore().restore(context, providers.get(0)));
  }
}