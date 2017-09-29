package agency.tango.skald.core;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import agency.tango.skald.core.errors.AuthError;
import agency.tango.skald.core.errors.PlaybackError;
import agency.tango.skald.core.listeners.OnAuthErrorListener;
import agency.tango.skald.core.listeners.OnErrorListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.core.models.TrackMetadata;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

public class SkaldMusicService {
  public static final String INTENT_ACTION = "auth_action";
  public static final String EXTRA_AUTH_DATA = "auth_data";
  public static final String EXTRA_PROVIDER_NAME = "provider_name";
  private static final int MAX_NUMBER_OF_PLAYERS = 2;

  private final List<OnAuthErrorListener> onAuthErrorListeners = new ArrayList<>();
  private final List<OnErrorListener> onErrorListeners = new ArrayList<>();
  private final List<OnPlaybackListener> onPlaybackListeners = new ArrayList<>();
  private final List<Provider> providers = new ArrayList<>();
  private final Timer timer = new Timer();

  private final Context context;
  private TLruCache<String, Player> playerCache;
  private Player currentPlayer;

  public SkaldMusicService(Context context, Provider... providers) {
    this(context);
    this.providers.addAll(Arrays.asList(providers));
  }

  public SkaldMusicService(Context context) {
    this.context = context.getApplicationContext();
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
  }

  public void addProvider(Provider provider) {
    if(!providers.contains(provider)) {
      providers.add(provider);
    }
  }

  public void removeProvider(Provider provider) {
    providers.remove(provider);
  }

  public Single<Object> play(final SkaldTrack skaldTrack) {
    return Single.create(new SingleOnSubscribe<Object>() {
      boolean playerInitialized = false;
      Provider currentProvider;
      Player previousPlayer;

      @Override
      public void subscribe(@NonNull final SingleEmitter<Object> emitter) throws Exception {
        if (currentPlayer != null) {
          currentPlayer.stop();
        }
        for (Provider provider : providers) {
          if (provider.canHandle(skaldTrack)) {
            currentProvider = provider;
            Player player = playerCache.get(provider.getProviderName());
            if (player != null) {
              playTrack(emitter, player);
            } else {
              initializePlayerAndPlay(emitter, provider);
            }
          }
        }

        emitter.setDisposable(new Disposable() {
          @Override
          public void dispose() {
            if (!playerInitialized && currentProvider != null) {
              playerCache.remove(currentProvider.getProviderName());
              currentPlayer = previousPlayer;
            }
          }

          @Override
          public boolean isDisposed() {
            return false;
          }
        });
      }

      private void playTrack(@NonNull SingleEmitter<Object> emitter, Player player) {
        player.play(skaldTrack);
        playerInitialized = true;
        currentPlayer = player;
        emitter.onSuccess(player);
      }

      private void initializePlayerAndPlay(@NonNull final SingleEmitter<Object> emitter,
          Provider provider) {
        try {
          Player player = provider.getPlayerFactory().getPlayer();
          previousPlayer = currentPlayer;
          currentPlayer = player;
          playerCache.put(provider.getProviderName(), player);
          player.addOnPlaybackListener(new OnPlayerPlaybackListener());
          player.addOnPlayerReadyListener(new OnPlayerReadyListener() {
            @Override
            public void onPlayerReady(Player player) {
              player.play(skaldTrack);
              playerInitialized = true;
              emitter.onSuccess(player);
            }
          });
        } catch (AuthException authException) {
          emitter.onError(authException);
        }
      }
    });
  }

  public Completable pause() {
    return Completable.create(new CompletableOnSubscribe() {
      @Override
      public void subscribe(@NonNull CompletableEmitter emitter) throws Exception {
        if (currentPlayer != null) {
          currentPlayer.pause();
        }
        emitter.onComplete();
      }
    });
  }

  public Completable resume() {
    return Completable.create(new CompletableOnSubscribe() {
      @Override
      public void subscribe(@NonNull CompletableEmitter emitter) throws Exception {
        if (currentPlayer != null) {
          currentPlayer.resume();
        }
        emitter.onComplete();
      }
    });
  }

  public Completable stop() {
    return Completable.create(new CompletableOnSubscribe() {
      @Override
      public void subscribe(@NonNull CompletableEmitter emitter) throws Exception {
        if (currentPlayer != null) {
          currentPlayer.stop();
        }
        emitter.onComplete();
      }
    });
  }

  public void release() {
    playerCache.evictAll();
    timer.cancel();
  }

  public void addOnErrorListner(OnErrorListener onErrorListener) {
    onErrorListeners.add(onErrorListener);
  }

  public void removeOnErrorListener() {
    onErrorListeners.remove(0);
  }

  public void addOnAuthErrorListener(OnAuthErrorListener onAuthErrorListener) {
    onAuthErrorListeners.add(onAuthErrorListener);
  }

  public void removeOnAuthErrorListener() {
    onAuthErrorListeners.remove(0);
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
        notifyAuthError(authException.getAuthError());
        //todo
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
        notifyAuthError(authException.getAuthError());
      }
    }
    return mergeLists(singles);
  }

  private void notifyAuthError(AuthError authError) {
    for (OnAuthErrorListener onAuthErrorListener : onAuthErrorListeners) {
      onAuthErrorListener.onAuthError(authError);
    }
  }

  private SkaldAuthStore getSkaldAuthStore(Provider provider) {
    return provider.getSkaldAuthStoreFactory().getSkaldAuthStore();
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

  private class OnPlayerPlaybackListener implements OnPlaybackListener {
    @Override
    public void onPlayEvent(TrackMetadata trackMetadata) {
      for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
        onPlaybackListener.onPlayEvent(trackMetadata);
      }
    }

    @Override
    public void onPauseEvent() {
      for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
        onPlaybackListener.onPauseEvent();
      }
    }

    @Override
    public void onResumeEvent() {
      for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
        onPlaybackListener.onResumeEvent();
      }
    }

    @Override
    public void onStopEvent() {
      for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
        onPlaybackListener.onStopEvent();
      }
    }

    @Override
    public void onError(PlaybackError playbackError) {
      for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
        onPlaybackListener.onError(playbackError);
      }
    }
  }
}