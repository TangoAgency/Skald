package agency.tango.skald.deezer.player;

import android.content.Context;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import agency.tango.skald.core.Player;
import agency.tango.skald.core.callbacks.SkaldOperationCallback;
import agency.tango.skald.core.listeners.OnErrorListener;
import agency.tango.skald.core.listeners.OnLoadingListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.deezer.authentication.DeezerAuthData;

public class SkaldDeezerPlayer implements Player {
  private final List<OnPlayerReadyListener> onPlayerReadyListeners = new ArrayList<>();
  private final DeezerPlayer deezerPlayer;

  public SkaldDeezerPlayer(Context context, DeezerAuthData deezerAuthData,
      OnErrorListener onErrorListener) {
    deezerPlayer = new DeezerPlayer(context, deezerAuthData.getDeezerConnect(), onErrorListener);
  }

  @Override
  public void play(@NonNull SkaldPlayableEntity playableEntity,
      SkaldOperationCallback operationCallback) {
    deezerPlayer.play(playableEntity, operationCallback);
  }

  @Override
  public void stop(SkaldOperationCallback skaldOperationCallback) {
    deezerPlayer.stop(skaldOperationCallback);
  }

  @Override
  public void pause(SkaldOperationCallback skaldOperationCallback) {
    deezerPlayer.pause(skaldOperationCallback);
  }

  @Override
  public void resume(SkaldOperationCallback skaldOperationCallback) {
    deezerPlayer.resume(skaldOperationCallback);
  }

  @Override
  public void release() {
    deezerPlayer.release();
  }

  @Override
  public boolean isPlaying() {
    return deezerPlayer.isPlaying();
  }

  @Override
  public void addOnPlayerReadyListener(@NonNull OnPlayerReadyListener onPlayerReadyListener) {
    onPlayerReadyListeners.add(onPlayerReadyListener);

    for (OnPlayerReadyListener onPlayerReadyExistingListener : onPlayerReadyListeners) {
      onPlayerReadyExistingListener.onPlayerReady(this);
    }
  }

  @Override
  public void removeOnPlayerReadyListener(@NonNull OnPlayerReadyListener onPlayerReadyListener) {
    onPlayerReadyListeners.remove(onPlayerReadyListener);
  }

  @Override
  public void addOnPlaybackListener(@NonNull OnPlaybackListener onPlaybackListener) {
    deezerPlayer.addOnPlayerReadyListener(onPlaybackListener);
  }

  @Override
  public void removeOnPlaybackListener(@NonNull OnPlaybackListener onPlaybackListener) {
    deezerPlayer.removeOnPlayerReadyListener(onPlaybackListener);
  }

  @Override
  public void addOnLoadingListener(@NonNull OnLoadingListener onLoadingListener) {
    deezerPlayer.addOnLoadingListener(onLoadingListener);
  }

  @Override
  public void removeOnLoadingListener(@NonNull OnLoadingListener onLoadingListener) {
    deezerPlayer.removeOnLoadingListener(onLoadingListener);
  }
}
