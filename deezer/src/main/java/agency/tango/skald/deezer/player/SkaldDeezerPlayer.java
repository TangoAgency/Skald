package agency.tango.skald.deezer.player;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import agency.tango.skald.core.Player;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.deezer.authentication.DeezerAuthData;

public class SkaldDeezerPlayer implements Player {
  private final List<OnPlayerReadyListener> onPlayerReadyListeners = new ArrayList<>();
  private final DeezerPlayer deezerPlayer;

  public SkaldDeezerPlayer(Context context, DeezerAuthData deezerAuthData) {
    deezerPlayer = new DeezerPlayer(context, deezerAuthData.getDeezerConnect());
  }

  @Override
  public void play(SkaldPlayableEntity skaldPlayableEntity) {
    if(skaldPlayableEntity instanceof SkaldTrack) {
      play((SkaldTrack) skaldPlayableEntity);
    }
    else if(skaldPlayableEntity instanceof SkaldPlaylist) {
      play((SkaldPlaylist) skaldPlayableEntity);
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

  private void play(SkaldTrack track) {
    deezerPlayer.play(track);
  }

  private void play(SkaldPlaylist playlist) {
    deezerPlayer.play(playlist);
  }
}
