package agency.tango.skald.core;

import java.util.List;

import agency.tango.skald.core.exceptions.AuthException;
import agency.tango.skald.core.cache.TLruCache;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.core.provider.Provider;
import agency.tango.skald.core.provider.ProviderName;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class SingleOnPlaySubscribe implements SingleOnSubscribe<Object> {
  private final SkaldMusicService skaldMusicService;
  private final SkaldTrack skaldTrack;
  private final List<OnPlaybackListener> onPlaybackListeners;
  private final TLruCache<ProviderName, Player> playerCache;
  private final List<Provider> providers;

  private boolean playerInitialized = false;
  private Player initializedPlayer;

  SingleOnPlaySubscribe(SkaldMusicService skaldMusicService, SkaldTrack skaldTrack,
      List<OnPlaybackListener> onPlaybackListeners, TLruCache<ProviderName, Player> playerCache,
      List<Provider> providers) {
    this.skaldMusicService = skaldMusicService;
    this.skaldTrack = skaldTrack;
    this.onPlaybackListeners = onPlaybackListeners;
    this.playerCache = playerCache;
    this.providers = providers;
  }

  @Override
  public void subscribe(@NonNull final SingleEmitter<Object> emitter) throws Exception {
    ProviderName currentProviderName = skaldMusicService.getCurrentProviderName();
    if(currentProviderName != null) {
      Player currentPlayer = playerCache.get(currentProviderName);
      if (currentPlayer != null) {
        currentPlayer.stop();
      }
    }
    for (Provider provider : providers) {
      if (provider.canHandle(skaldTrack)) {
        ProviderName providerName = provider.getProviderName();
        Player player = playerCache.get(providerName);
        if (player != null) {
          playTrack(emitter, player, providerName);
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

  private void playTrack(@NonNull SingleEmitter<Object> emitter, Player player,
      ProviderName providerName) {
    playerInitialized = true;
    player.play(skaldTrack);
    skaldMusicService.setCurrentProviderName(providerName);
    emitter.onSuccess(player);
  }

  private void initializePlayerAndPlay(@NonNull final SingleEmitter<Object> emitter,
      final Provider provider, final ProviderName providerName) {
    try {
      initializedPlayer = provider.getPlayerFactory().getPlayer();
      initializedPlayer.addOnPlaybackListener(new OnPlayerPlaybackListener(onPlaybackListeners));
      initializedPlayer.addOnPlayerReadyListener(new OnPlayerReadyListener() {
        @Override
        public void onPlayerReady(Player player) {
          playerInitialized = true;
          playerCache.put(provider.getProviderName(), initializedPlayer);
          player.play(skaldTrack);
          skaldMusicService.setCurrentProviderName(providerName);
          emitter.onSuccess(player);
        }
      });
    } catch (AuthException authException) {
      emitter.onError(authException);
    }
  }
}
