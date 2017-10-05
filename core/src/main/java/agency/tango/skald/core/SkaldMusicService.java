package agency.tango.skald.core;

import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import agency.tango.skald.core.listeners.OnErrorListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class SkaldMusicService {
  public static final String INTENT_ACTION = "auth_action";
  public static final String EXTRA_AUTH_DATA = "auth_data";
  public static final String EXTRA_PROVIDER_NAME = "provider_name";
  private static final int MAX_NUMBER_OF_PLAYERS = 2;

  private final List<OnErrorListener> onErrorListeners = new ArrayList<>();
  private final List<OnPlaybackListener> onPlaybackListeners = new ArrayList<>();
  private final List<Provider> providers;
  private final Timer timer = new Timer();

  private TLruCache<String, Player> playerCache;
  private String currentPlayerKey;

  public SkaldMusicService(Context context) {
    this.providers = Skald.singleton().providers();

    this.playerCache = new TLruCache<>(MAX_NUMBER_OF_PLAYERS,
        new SkaldLruCache.CacheItemRemovedListener<String, Player>() {
          @Override
          public void release(String key, Player player) {
            player.release();
          }
        });

    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        playerCache.evictTo(1, TimeUnit.MINUTES);
      }
    }, 10000, 10000);

    LocalBroadcastManager
        .getInstance(context.getApplicationContext())
        .registerReceiver(new AuthDataReceiver(this.providers), new IntentFilter(INTENT_ACTION));
  }

  public synchronized Single<Object> play(final SkaldTrack skaldTrack) {
    return Single.create(new SingleOnPlaySubscribe(this, skaldTrack, onPlaybackListeners,
        playerCache, providers));
  }

  public Completable pause() {
    return Completable.create(new CompletableOnSubscribe() {
      @Override
      public void subscribe(@NonNull CompletableEmitter emitter) throws Exception {
        if (currentPlayerKey != null) {
          playerCache.get(currentPlayerKey).pause();
        }
        emitter.onComplete();
      }
    });
  }

  public Completable resume() {
    return Completable.create(new CompletableOnSubscribe() {
      @Override
      public void subscribe(@NonNull CompletableEmitter emitter) throws Exception {
        if (currentPlayerKey != null) {
          playerCache.get(currentPlayerKey).resume();
        }
        emitter.onComplete();
      }
    });
  }

  public Completable stop() {
    return Completable.create(new CompletableOnSubscribe() {
      @Override
      public void subscribe(@NonNull CompletableEmitter emitter) throws Exception {
        if (currentPlayerKey != null) {
          playerCache.get(currentPlayerKey).stop();
        }
        emitter.onComplete();
      }
    });
  }

  public void release() {
    playerCache.evictAll();
    timer.cancel();
  }

  public void addOnErrorListener(OnErrorListener onErrorListener) {
    onErrorListeners.add(onErrorListener);
  }

  public void removeOnErrorListener() {
    onErrorListeners.remove(0);
  }

  public void addOnPlaybackListener(OnPlaybackListener onPlaybackListener) {
    onPlaybackListeners.add(onPlaybackListener);
  }

  public void removeOnPlaybackListener() {
    onPlaybackListeners.remove(0);
  }

  public Single<List<SkaldTrack>> searchTracks(String query) {
    List<Single<List<SkaldTrack>>> singles = new ArrayList<>();
    for (Provider provider : providers) {
      try {
        singles.add(getSearchService(provider).searchForTracks(query));
      } catch (AuthException authException) {
        authException.printStackTrace();
      }
    }
    return mergeLists(singles);
  }

  public Single<List<SkaldPlaylist>> searchPlayLists(String query) {
    List<Single<List<SkaldPlaylist>>> singles = new ArrayList<>();
    for (Provider provider : providers) {
      try {
        singles.add(getSearchService(provider).searchForPlaylists(query));
      } catch (AuthException authException) {
        authException.printStackTrace();
      }
    }
    return mergeLists(singles);
  }

  String getCurrentPlayerKey() {
    return currentPlayerKey;
  }

  void setCurrentPlayerKey(String playerKey) {
    this.currentPlayerKey = playerKey;
  }

  private SearchService getSearchService(Provider provider) throws AuthException {
    return provider.getSearchServiceFactory().getSearchService();
  }

  private <T> Single<List<T>> mergeLists(List<Single<List<T>>> singlesList) {
    return Single.merge(singlesList)
        .toList()
        .map(new Function<List<List<T>>, List<T>>() {
          @Override
          public List<T> apply(@NonNull List<List<T>> lists) throws Exception {
            List<T> mergedList = new ArrayList<>();
            for (List<T> list : lists) {
              mergedList.addAll(list);
            }
            return mergedList;
          }
        });
  }
}