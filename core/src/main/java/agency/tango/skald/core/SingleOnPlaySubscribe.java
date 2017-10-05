package agency.tango.skald.core;

import java.util.List;

import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.models.SkaldTrack;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;

public abstract class SingleOnPlaySubscribe<T> implements SingleOnSubscribe<T> {
  private final SkaldTrack skaldTrack;
  private final List<OnPlaybackListener> onPlaybackListeners;
  private final TLruCache<String, Player> playerCache;
  private final SkaldMusicService skaldMusicService;

  private boolean playerInitialized = false;

  SingleOnPlaySubscribe(SkaldTrack skaldTrack, List<OnPlaybackListener> onPlaybackListeners,
      TLruCache<String, Player> playerCache, SkaldMusicService skaldMusicService) {
    this.skaldTrack = skaldTrack;
    this.onPlaybackListeners = onPlaybackListeners;
    this.playerCache = playerCache;
    this.skaldMusicService = skaldMusicService;
  }

  void playTrack(@NonNull SingleEmitter<Object> emitter, Player player,
      String initializedPlayerKey) {
    player.play(skaldTrack);
    skaldMusicService.setCurrentPlayerKey(initializedPlayerKey);
    playerInitialized = true;
    emitter.onSuccess(player);
  }

  void initializePlayerAndPlay(@NonNull final SingleEmitter<Object> emitter,
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

  boolean isPlayerInitialized() {
    return playerInitialized;
  }
}
