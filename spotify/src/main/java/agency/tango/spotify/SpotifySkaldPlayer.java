package agency.tango.spotify;

import android.content.Context;
import android.util.Log;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import agency.tango.core.Player;
import agency.tango.core.PlayerConfig;
import agency.tango.core.listeners.ErrorListener;
import agency.tango.core.listeners.MetadataListener;
import agency.tango.core.listeners.PlaybackListener;
import agency.tango.core.listeners.PlayerReadyListener;
import agency.tango.core.models.SkaldPlaylist;
import agency.tango.core.models.SkaldTrack;

public class SpotifySkaldPlayer implements Player {
  private SpotifyPlayer spotifyPlayer;
  private PlayerReadyListener playerReadyListener;
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
    if (spotifyPlayer.getPlaybackState().isPlaying) {
      spotifyPlayer.pause(callback);
    }
  }

  @Override
  public void resume() {
    if (!spotifyPlayer.getPlaybackState().isPlaying) {
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

  @Override
  public void removeErrorListener(ErrorListener errorListener) {

  }

  @Override
  public void removePlaybackListener(PlaybackListener playbackListener) {
  }

  @Override
  public void removePlayerReadyListener(PlayerReadyListener playerReadyListener) {

  }

  @Override
  public void addPlayerReadyListener(PlayerReadyListener playerReadyListener) {
    this.playerReadyListener = playerReadyListener;
  }

  @Override
  public void removeMetadataListener(MetadataListener metadataListener) {
  }

  @Override
  public void initializePlayer(PlayerConfig playerConfig, String clientId, Context context) {
    Config spotifyConfig = new Config(context.getApplicationContext(), playerConfig.getOauthToken(),
        clientId);

    Spotify.getPlayer(spotifyConfig, context.getApplicationContext(),
        new SpotifyPlayer.InitializationObserver() {
          @Override
          public void onInitialized(final SpotifyPlayer player) {
            spotifyPlayer = player;
            Log.i("SpotifyPlayer", "Player initialized");
            if (playerReadyListener == null) {
              Log.e("Player", "ADD PlayerReadyListener to initialize SpotifyPlayer");
            } else {
              player.addConnectionStateCallback(new SpotifyConnectionStateCallback() {
                @Override
                public void onLoggedIn() {
                  playerReadyListener.onPlayerReady(SpotifySkaldPlayer.this);
                }

                @Override
                public void onLoginFailed(Error error) {
                  playerReadyListener.onError();
                }
              });
            }
          }

          @Override
          public void onError(Throwable throwable) {
            Log.e("SpotifyPlayer", "Error during initialization of the player", throwable);
          }
        });
  }
}
