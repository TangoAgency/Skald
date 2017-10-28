package agency.tango.skald.deezer.player;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import agency.tango.skald.core.Player;
import agency.tango.skald.core.callbacks.SkaldOperationCallback;
import agency.tango.skald.core.listeners.OnLoadingListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.deezer.authentication.DeezerAuthData;

public class SkaldDeezerPlayer implements Player {
  private final List<OnPlayerReadyListener> onPlayerReadyListeners = new ArrayList<>();
  private final DeezerPlayer deezerPlayer;

  public SkaldDeezerPlayer(Context context, DeezerAuthData deezerAuthData) {
    deezerPlayer = new DeezerPlayer(context, deezerAuthData.getDeezerConnect());
  }

  @Override
  public void play(SkaldPlayableEntity playableEntity, SkaldOperationCallback operationCallback) {
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
  public void addOnPlayerReadyListener(OnPlayerReadyListener onPlayerReadyListener) {
    onPlayerReadyListeners.add(onPlayerReadyListener);

    for (OnPlayerReadyListener onPlayerReadyExistingListener : onPlayerReadyListeners) {
      onPlayerReadyExistingListener.onPlayerReady(this);
    }
  }

  @Override
  public void removeOnPlayerReadyListener() {
    onPlayerReadyListeners.remove(0);
  }

  @Override
  public void addOnPlaybackListener(OnPlaybackListener onPlaybackListener) {
    deezerPlayer.addOnPlayerReadyListener(onPlaybackListener);
  }

  @Override
  public void removeOnPlaybackListener() {
    deezerPlayer.removeOnPlayerReadyListener();
  }

  @Override
  public void addOnLoadingListener(OnLoadingListener onLoadingListener) {
    deezerPlayer.addOnLoadingListener(onLoadingListener);
  }

  @Override
  public void removeOnLoadingListener() {
    deezerPlayer.removeOnLoadingListener();
  }
}
