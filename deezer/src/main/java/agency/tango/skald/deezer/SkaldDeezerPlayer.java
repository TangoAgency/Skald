package agency.tango.skald.deezer;

import android.content.Context;

import com.deezer.sdk.network.request.event.DeezerError;

import java.util.ArrayList;
import java.util.List;

import agency.tango.skald.core.Player;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;

class SkaldDeezerPlayer implements Player {
  private final List<OnPlayerReadyListener> onPlayerReadyListeners = new ArrayList<>();
  private DeezerPlayer deezerPlayer;

  SkaldDeezerPlayer(Context context, DeezerAuthData deezerAuthData) {
    deezerPlayer = new DeezerPlayer(context, deezerAuthData.getDeezerConnect());
  }

  @Override
  public void play(SkaldTrack track) {
    try {
      deezerPlayer.play(track);
    } catch (DeezerError deezerError) {
      deezerError.getMessage();
      deezerError.printStackTrace();
    }
  }

  @Override
  public void play(SkaldPlaylist playlist) {
    try {
      deezerPlayer.play(playlist);
    } catch (DeezerError deezerError) {
      deezerError.getMessage();
      deezerError.printStackTrace();
    }
  }

  @Override
  public void stop() {
    deezerPlayer.stop();
  }

  @Override
  public void pause() {
    deezerPlayer.pause();
  }

  @Override
  public void resume() {
    deezerPlayer.resume();
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
  public void addPlayerReadyListener(OnPlayerReadyListener onPlayerReadyListener) {
    onPlayerReadyListeners.add(onPlayerReadyListener);

    for (OnPlayerReadyListener onPlayerReadyExistingListener : onPlayerReadyListeners) {
      onPlayerReadyExistingListener.onPlayerReady(this);
    }
  }

  @Override
  public void removePlayerReadyListener(OnPlayerReadyListener onPlayerReadyListener) {
    onPlayerReadyListeners.remove(onPlayerReadyListener);
  }

  @Override
  public void addOnPlaybackListener(OnPlaybackListener onPlaybackListener) {

  }

  @Override
  public void removeOnPlaybackListener(OnPlaybackListener onPlaybackListener) {

  }
}
