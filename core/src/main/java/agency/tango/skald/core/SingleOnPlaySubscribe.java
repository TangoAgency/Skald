package agency.tango.skald.core;

import java.util.List;

import agency.tango.skald.core.cache.TLruCache;
import agency.tango.skald.core.exceptions.AuthException;
import agency.tango.skald.core.listeners.OnLoadingListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.provider.Provider;
import agency.tango.skald.core.provider.ProviderName;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class SingleOnPlaySubscribe implements SingleOnSubscribe<Object> {
  private final SkaldMusicService skaldMusicService;
  private final SkaldPlayableEntity skaldPlayableEntity;
  private final List<OnPlaybackListener> onPlaybackListeners;
  private final List<OnLoadingListener> onLoadingListeners;
  private final TLruCache<ProviderName, Player> playerCache;
  private final List<Provider> providers;

  private boolean playerInitialized = false;
  private Player initializedPlayer;

  SingleOnPlaySubscribe(SkaldMusicService skaldMusicService,
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
  public void subscribe(@NonNull final SingleEmitter<Object> emitter) throws Exception {
    ProviderName currentProviderName = skaldMusicService.getCurrentProviderName();
    if (currentProviderName != null) {
      Player currentPlayer = playerCache.get(currentProviderName);
      if (currentPlayer != null) {
        currentPlayer.stop();
      }
    }
    for (Provider provider : providers) {
      if (provider.canHandle(skaldPlayableEntity)) {
        ProviderName providerName = provider.getProviderName();
        Player player = playerCache.get(providerName);
        if (player != null) {
          play(emitter, player, providerName);
        } else {
          initializePlayerAndPlay(emitter, provider, providerName);
        }
      }
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

  private void play(@NonNull SingleEmitter<Object> emitter, Player player,
      ProviderName providerName) {
    playerInitialized = true;
    player.play(skaldPlayableEntity);
    skaldMusicService.setCurrentProviderName(providerName);
    emitter.onSuccess(player);
  }

  private void initializePlayerAndPlay(@NonNull final SingleEmitter<Object> emitter,
      final Provider provider, final ProviderName providerName) {
    try {
      initializedPlayer = provider.getPlayerFactory().getPlayer();
      initializedPlayer.addOnPlaybackListener(new OnPlayerPlaybackListener(onPlaybackListeners));
      initializedPlayer.addOnLoadingListener(new OnLoadingListener() {
        @Override
        public void onLoading() {
          for(OnLoadingListener onLoadingListener : onLoadingListeners) {
            onLoadingListener.onLoading();
          }
        }
      });
      initializedPlayer.addOnPlayerReadyListener(new OnPlayerReadyListener() {
        @Override
        public void onPlayerReady(Player player) {
          playerInitialized = true;
          playerCache.put(provider.getProviderName(), initializedPlayer);
          player.play(skaldPlayableEntity);
          skaldMusicService.setCurrentProviderName(providerName);
          emitter.onSuccess(player);
        }
      });
    } catch (AuthException authException) {
      emitter.onError(authException);
    }
  }
}
