package agency.tango.skald.deezer;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.player.TrackPlayer;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;
import com.deezer.sdk.player.networkcheck.WifiAndMobileNetworkStateChecker;

import java.util.ArrayList;
import java.util.List;

import agency.tango.skald.core.Player;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;

public class SkaldDeezerPlayer implements Player {
  private static final String TAG = SkaldDeezerPlayer.class.getSimpleName();

  private final List<OnPlayerReadyListener> onPlayerReadyListeners = new ArrayList<>();
  private TrackPlayer player;

  public SkaldDeezerPlayer(Context context, DeezerAuthData deezerAuthData) {
    try {
      player = new TrackPlayer((Application) context.getApplicationContext(),
          deezerAuthData.getDeezerConnect(), new WifiAndMobileNetworkStateChecker());
    } catch (TooManyPlayersExceptions | DeezerError tooManyPlayersExceptions) {
      tooManyPlayersExceptions.printStackTrace();
    }
    Log.d(TAG, "DeezerPlayer initialized");
  }

  @Override
  public void play(SkaldTrack track) {
    Uri uri = track.getUri();
    String stringUri = uri.getPathSegments().get(uri.getPathSegments().size() - 1);
    long trackId = Long.parseLong(stringUri);
    player.playTrack(trackId);
  }

  @Override
  public void play(SkaldPlaylist playlist) {

  }

  @Override
  public void stop() {

  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void release() {

  }

  @Override
  public void addPlayerReadyListener(OnPlayerReadyListener onPlayerReadyListener) {
    onPlayerReadyListeners.add(onPlayerReadyListener);

    for(OnPlayerReadyListener onPlayerReadyExistingListener : onPlayerReadyListeners) {
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
