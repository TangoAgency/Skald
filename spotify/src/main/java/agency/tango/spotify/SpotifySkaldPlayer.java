package agency.tango.spotify;

import android.content.Context;
import android.util.Log;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import agency.tango.core.Player;
import agency.tango.core.listeners.ErrorListener;
import agency.tango.core.listeners.MetadataListener;
import agency.tango.core.listeners.PlaybackListener;
import agency.tango.core.models.SkaldPlaylist;
import agency.tango.core.models.SkaldTrack;

public class SpotifySkaldPlayer implements Player {
  private SpotifyPlayer spotifyPlayer;
  private final SpotifyOperationCallback callback = new SpotifyOperationCallback();

  @Override
  public void play(SkaldTrack track) {
    spotifyPlayer.playUri(callback, track.getUri(), 0, 0);
  }

  @Override
  public void play(SkaldPlaylist playlist) {

  }

  @Override
  public void stop() {
  }

  @Override
  public void pause() {
    if(spotifyPlayer.getPlaybackState().isPlaying){
      spotifyPlayer.pause(callback);
    }
  }

  @Override
  public void resume() {
    if(!spotifyPlayer.getPlaybackState().isPlaying) {
      spotifyPlayer.resume(callback);
    }
  }

  @Override
  public void release() {
    //TODO must call Spotify.destroyPlayer
  }

  @Override
  public void addErrorListener(ErrorListener errorListener) {

  }

  @Override
  public void addPlaybackListener(PlaybackListener playbackListener) {

  }

  @Override
  public void addMetadataListener(MetadataListener metadataListener) {

  }

  public void initializePlayer(Config playerConfig, Context context) {
    Spotify.getPlayer(playerConfig, context.getApplicationContext(),
        new SpotifyPlayer.InitializationObserver() {
          @Override
          public void onInitialized(SpotifyPlayer player) {
            spotifyPlayer = player;
            Log.i("SpotifyPlayer", "Player initialized");
          }

          @Override
          public void onError(Throwable throwable) {
            Log.e("SpotifyPlayer", "Error during initialization of the player", throwable);
          }
        });
  }
}
