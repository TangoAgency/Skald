package agency.tango.skald.core;

import android.util.Log;

import java.util.List;

import agency.tango.skald.core.cache.TLruCache;
import agency.tango.skald.core.callbacks.SkaldCoreOperationCallback;
import agency.tango.skald.core.callbacks.SkaldOperationCallback;
import agency.tango.skald.core.exceptions.AuthException;
import agency.tango.skald.core.listeners.OnLoadingListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.provider.Provider;
import agency.tango.skald.core.provider.ProviderName;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

class CompletableOnPlaySubscribe implements CompletableOnSubscribe {
  private final SkaldMusicService skaldMusicService;
  private final SkaldPlayableEntity skaldPlayableEntity;
  private final List<OnPlaybackListener> onPlaybackListeners;
  private final List<OnLoadingListener> onLoadingListeners;
  private final TLruCache<ProviderName, Player> playerCache;
  private final List<Provider> providers;

  private boolean playerInitialized = false;
  private Player initializedPlayer;

  CompletableOnPlaySubscribe(SkaldMusicService skaldMusicService,
      SkaldPlayableEntity skaldPlayableEntity, List<OnPlaybackListener> onPlaybackListeners,
      List<OnLoadingListener> onLoadingListeners, TLruCache<ProviderName, Player> playerCache,
      List<Provider> providers) {
    this.skaldMusicService = skaldMusicService;
    this.skaldPlayableEntity = skaldPlayableEntity;
    this.onPlaybackListeners = onPlaybackListeners;
    this.onLoadingListeners = onLoadingListeners;
    this.playerCache = playerCache;
    this.providers = providers;
  }

  @Override
  public void subscribe(@NonNull final CompletableEmitter emitter) throws Exception {
    ProviderName currentProviderName = skaldMusicService.getCurrentProviderName();
    if (isAnotherPlayerPlaying(currentProviderName)) {
      Player currentPlayer = playerCache.get(currentProviderName);
      if (currentPlayer != null) {
        currentPlayer.stop(new SkaldOperationCallback() {
          @Override
          public void onSuccess() {
            play(emitter);
          }

          @Override
          public void onError() {
            Log.e(this.getClass().getSimpleName(), "Error during stopping");
          }
        });
      }
    } else {
      play(emitter);
    }

    emitter.setDisposable(new Disposable() {
      @Override
      public void dispose() {
        if (!playerInitialized && initializedPlayer != null) {
          initializedPlayer.release();
        }
      }

      @Override
      public boolean isDisposed() {
        return false;
      }
    });
  }

  private boolean isAnotherPlayerPlaying(ProviderName currentProviderName) {
    return currentProviderName != null
        && skaldMusicService.shouldPlayerBeChanged(skaldPlayableEntity)
        && skaldMusicService.isPlaying();
  }

  private void play(@NonNull CompletableEmitter emitter) {
    for (Provider provider : providers) {
      if (provider.canHandle(skaldPlayableEntity)) {
        ProviderName providerName = provider.getProviderName();
        Player player = playerCache.get(providerName);
        if (player != null) {
          playEntity(emitter, player, providerName);
        } else {
          initializePlayerAndPlay(emitter, provider, providerName);
        }
      }
    }
  }

  private void playEntity(@NonNull CompletableEmitter emitter, Player player,
      ProviderName providerName) {
    playerInitialized = true;
    player.play(skaldPlayableEntity, new SkaldCoreOperationCallback(emitter));
    skaldMusicService.setCurrentProviderName(providerName);
  }

  private void initializePlayerAndPlay(@NonNull final CompletableEmitter emitter,
      final Provider provider, final ProviderName providerName) {
    try {
      initializedPlayer = provider.getPlayerFactory().getPlayer();
      initializedPlayer.addOnPlaybackListener(new OnPlayerPlaybackListener(onPlaybackListeners));
      initializedPlayer.addOnLoadingListener(new OnLoadingListener() {
        @Override
        public void onLoading() {
          for (OnLoadingListener onLoadingListener : onLoadingListeners) {
            onLoadingListener.onLoading();
          }
        }
      });
      initializedPlayer.addOnPlayerReadyListener(new OnPlayerReadyListener() {
        @Override
        public void onPlayerReady(Player player) {
          playerInitialized = true;
          playerCache.put(provider.getProviderName(), initializedPlayer);
          player.play(skaldPlayableEntity, new SkaldCoreOperationCallback(emitter));
          skaldMusicService.setCurrentProviderName(providerName);
        }
      });
    } catch (AuthException authException) {
      emitter.onError(authException);
    }
  }
}
