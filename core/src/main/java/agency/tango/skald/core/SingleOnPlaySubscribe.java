package agency.tango.skald.core;

import java.util.List;

import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.models.SkaldTrack;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class SingleOnPlaySubscribe implements SingleOnSubscribe<Object> {
  private final SkaldMusicService skaldMusicService;
  private final SkaldTrack skaldTrack;
  private final List<OnPlaybackListener> onPlaybackListeners;
  private final TLruCache<String, Player> playerCache;
  private final List<Provider> providers;

  private boolean playerInitialized = false;
  private String initializedPlayerKey;

  SingleOnPlaySubscribe(SkaldMusicService skaldMusicService, SkaldTrack skaldTrack,
      List<OnPlaybackListener> onPlaybackListeners, TLruCache<String, Player> playerCache,
      List<Provider> providers) {
    this.skaldMusicService = skaldMusicService;
    this.skaldTrack = skaldTrack;
    this.onPlaybackListeners = onPlaybackListeners;
    this.playerCache = playerCache;
    this.providers = providers;
  }

  @Override
  public void subscribe(@NonNull final SingleEmitter<Object> emitter) throws Exception {
    if (skaldMusicService.getCurrentPlayerKey() != null) {
      playerCache.get(skaldMusicService.getCurrentPlayerKey()).stop();
    }
    for (Provider provider : providers) {
      if (provider.canHandle(skaldTrack)) {
        initializedPlayerKey = provider.getProviderName();
        Player player = playerCache.get(initializedPlayerKey);
        if (player != null) {
          playTrack(emitter, player, initializedPlayerKey);
        } else {
          initializePlayerAndPlay(emitter, provider, initializedPlayerKey);
        }
      }
    }

    emitter.setDisposable(new Disposable() {
      @Override
      public void dispose() {
        if (!playerInitialized) {
          playerCache.remove(initializedPlayerKey);
        }
      }

      @Override
      public boolean isDisposed() {
        return false;
      }
    });
  }

  private void playTrack(@NonNull SingleEmitter<Object> emitter, Player player,
      String initializedPlayerKey) {
    player.play(skaldTrack);
    skaldMusicService.setCurrentPlayerKey(initializedPlayerKey);
    playerInitialized = true;
    emitter.onSuccess(player);
  }

  private void initializePlayerAndPlay(@NonNull final SingleEmitter<Object> emitter,
      final Provider provider, final String initializedPlayerKey) {
    try {
      Player player = provider.getPlayerFactory().getPlayer();
      playerCache.put(provider.getProviderName(), player);
      player.addOnPlaybackListener(new OnPlayerPlaybackListener(onPlaybackListeners));
      player.addOnPlayerReadyListener(new OnPlayerReadyListener() {
        @Override
        public void onPlayerReady(Player player) {
          playerInitialized = true;
          player.play(skaldTrack);
          skaldMusicService.setCurrentPlayerKey(initializedPlayerKey);
          emitter.onSuccess(player);
        }
      });
    } catch (AuthException authException) {
      emitter.onError(authException);
    }
  }
}
