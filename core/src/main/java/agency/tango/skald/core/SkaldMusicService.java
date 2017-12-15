package agency.tango.skald.core;

import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import agency.tango.skald.core.bus.LoginEvent;
import agency.tango.skald.core.bus.SkaldBus;
import agency.tango.skald.core.cache.TLruCache;
import agency.tango.skald.core.callbacks.SkaldCoreOperationCallback;
import agency.tango.skald.core.exceptions.AuthException;
import agency.tango.skald.core.listeners.OnErrorListener;
import agency.tango.skald.core.listeners.OnLoadingListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.core.models.SkaldUser;
import agency.tango.skald.core.provider.Provider;
import agency.tango.skald.core.provider.ProviderName;
import io.reactivex.Completable;
import io.reactivex.Single;

public class SkaldMusicService {
  public static final String INTENT_ACTION = "auth_action";
  public static final String INTENT_ACTION_ERROR = "auth_action_error";
  public static final String EXTRA_AUTH_DATA = "auth_data";
  public static final String EXTRA_PROVIDER_NAME = "provider_name";
  private static final String TAG = SkaldMusicService.class.getSimpleName();
  private static final int MAX_NUMBER_OF_PLAYERS = 2;

  private final List<OnErrorListener> onErrorListeners = new ArrayList<>();
  private final List<OnPlaybackListener> onPlaybackListeners = new ArrayList<>();
  private final List<OnLoadingListener> onLoadingListeners = new ArrayList<>();
  private final List<Provider> providers = Skald.instance().providers();
  private final Timer timer = new Timer();
  private final SkaldBus skaldBus = SkaldBus.getInstance();
  private final TLruCache<ProviderName, Player> playerCache = new TLruCache<>(MAX_NUMBER_OF_PLAYERS,
      (key, player) -> player.release());
  private final LogoutEventObserver logoutEventObserver = new LogoutEventObserver(playerCache,
      this);

  private ProviderName currentProviderName;

  public SkaldMusicService(Context context) {
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        playerCache.evictTo(60, TimeUnit.SECONDS);
      }
    }, 10000, 10000);

    LocalBroadcastManager
        .getInstance(context.getApplicationContext())
        .registerReceiver(new AuthDataReceiver(this.providers), new IntentFilter(INTENT_ACTION));

    skaldBus.observable(LoginEvent.class)
        .subscribe(logoutEventObserver);
  }

  public synchronized Completable play(final SkaldPlayableEntity skaldPlayableEntity) {
    return Completable.create(new CompletableOnPlaySubscribe(this, skaldPlayableEntity,
        onPlaybackListeners, onLoadingListeners, onErrorListeners, playerCache, providers));
  }

  public Completable pause() {
    return Completable.create(emitter -> {
      if (currentProviderName != null) {
        getCurrentPlayer().pause(new SkaldCoreOperationCallback(emitter));
      }
    });
  }

  public Completable resume() {
    return Completable.create(emitter -> {
      if (currentProviderName != null) {
        getCurrentPlayer().resume(new SkaldCoreOperationCallback(emitter));
      }
    });
  }

  public Completable stop() {
    return Completable.create(emitter -> {
      if (currentProviderName != null) {
        getCurrentPlayer().stop(new SkaldCoreOperationCallback(emitter));
      }
    });
  }

  public void release() {
    logoutEventObserver.dispose();
    playerCache.evictAll();
    timer.cancel();
  }

  public void addOnErrorListener(OnErrorListener onErrorListener) {
    onErrorListeners.add(onErrorListener);
  }

  public void removeOnErrorListener(OnErrorListener onErrorListener) {
    onErrorListeners.remove(onErrorListener);
  }

  public void addOnPlaybackListener(OnPlaybackListener onPlaybackListener) {
    onPlaybackListeners.add(onPlaybackListener);
  }

  public void removeOnPlaybackListener(OnPlaybackListener onPlaybackListener) {
    onPlaybackListeners.remove(onPlaybackListener);
  }

  public void addOnLoadingListener(OnLoadingListener onLoadingListener) {
    onLoadingListeners.add(onLoadingListener);
  }

  public void removeOnLoadingListener(OnLoadingListener onLoadingListener) {
    onLoadingListeners.remove(onLoadingListener);
  }

  public Single<List<SkaldTrack>> searchTracks(String query) {
    List<Single<List<SkaldTrack>>> tracks = new ArrayList<>();
    for (Provider provider : providers) {
      try {
        tracks.add(getSearchService(provider).searchForTracks(query));
      } catch (AuthException authException) {
        logProviderNotAuthenticatedWarning(provider);
      }
    }
    return mergeLists(tracks);
  }

  public Single<List<SkaldPlaylist>> searchPlayLists(String query) {
    List<Single<List<SkaldPlaylist>>> playlists = new ArrayList<>();
    for (Provider provider : providers) {
      try {
        playlists.add(getSearchService(provider).searchForPlaylists(query));
      } catch (AuthException authException) {
        logProviderNotAuthenticatedWarning(provider);
      }
    }
    return mergeLists(playlists);
  }

  public Single<List<SkaldUser>> getCurrentUsers() {
    List<Single<SkaldUser>> users = new ArrayList<>();
    for (Provider provider : providers) {
      try {
        users.add(getUser(provider));
      } catch (AuthException authException) {
        logProviderNotAuthenticatedWarning(provider);
      }
    }
    return Single.merge(users)
        .toList();
  }

  ProviderName getCurrentProviderName() {
    return currentProviderName;
  }

  void setCurrentProviderName(ProviderName providerName) {
    this.currentProviderName = providerName;
  }

  boolean shouldPlayerBeChanged(SkaldPlayableEntity skaldPlayableEntity) {
    for (Provider provider : providers) {
      if (provider.canHandle(skaldPlayableEntity)) {
        return !provider.getProviderName().equals(currentProviderName);
      }
    }
    return false;
  }

  boolean isPlaying() {
    return getCurrentPlayer().isPlaying();
  }

  private Player getCurrentPlayer() {
    return playerCache.get(currentProviderName);
  }

  private Single<SkaldUser> getUser(Provider provider) throws AuthException {
    return getUserService(provider).getUser();
  }

  private SearchService getSearchService(Provider provider) throws AuthException {
    return provider.getSearchServiceFactory().getSearchService();
  }

  private UserService getUserService(Provider provider) throws AuthException {
    return provider.getUserServiceFactory().getUserService();
  }

  private void logProviderNotAuthenticatedWarning(Provider provider) {
    Log.w(TAG, String.format("%s is not authenticated", provider.getProviderName().getName()));
  }

  private <T> Single<List<T>> mergeLists(List<Single<List<T>>> singlesList) {
    return Single.merge(singlesList)
        .flatMapIterable(list -> list)
        .toList();
  }
}