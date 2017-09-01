package agency.tango.skald.spotify;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player.NotificationCallback;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.ArrayList;
import java.util.List;

import agency.tango.skald.core.OnPlayerReadyListener;
import agency.tango.skald.core.Player;
import agency.tango.skald.core.models.SkaldTrack;

public class SkaldSpotifyPlayer implements Player {
  private static final String TAG = SkaldSpotifyPlayer.class.getSimpleName();
  private SpotifyPlayer spotifyPlayer;
  private final List<OnPlayerReadyListener> onPlayerReadyListeners = new ArrayList<>();

  private boolean isInitialized = false;
  private SkaldTrack trackToPlay = null;

  public SkaldSpotifyPlayer(Context context, String clientId) {
    String oauthToken = "BQBrS5WFqCCVihr6HK5-iYDRpnVxwW1oiW66w-WAKAK5JJ-hvkXd62kVQcamRHjUGhZ-nRDLgTzZSQnBhRQ_GXYSSHdimrmU2lSyYF6TheGci14h2Ig8wc0hGor3TokqjTl1zKaSYyg8k3LK41M2d_eHVvIPlRQdG55XNRolarSg8eRXyXcNpcmZfpc7";
    final Config playerConfig = new Config(context, oauthToken, clientId);

    spotifyPlayer = Spotify.getPlayer(playerConfig, this,
        new SpotifyPlayer.InitializationObserver() {
          @Override
          public void onInitialized(SpotifyPlayer spotifyPlayer) {
            spotifyPlayer.addNotificationCallback(new NotificationCallback() {
              @Override
              public void onPlaybackEvent(PlayerEvent playerEvent) {

              }

              @Override
              public void onPlaybackError(Error error) {

              }
            });

            spotifyPlayer.addConnectionStateCallback(new ConnectionStateCallback() {
              @Override
              public void onLoggedIn() {
                //isInitialized = true;
                //if(trackToPlay != null) {
                //  play(trackToPlay);
                //  trackToPlay = null;
                //}
                Log.d(TAG, "onLoggedIn");
                for(OnPlayerReadyListener onPlayerReadyListener : onPlayerReadyListeners) {
                  onPlayerReadyListener.onPlayerReady(SkaldSpotifyPlayer.this);
                }
              }

              @Override
              public void onLoggedOut() {

              }

              @Override
              public void onLoginFailed(Error error) {

              }

              @Override
              public void onTemporaryError() {

              }

              @Override
              public void onConnectionMessage(String s) {

              }
            });
          }

          @Override
          public void onError(Throwable throwable) {
            Log.e(TAG, "Could not initialize player", throwable);
          }
        });
  }

  @Override
  public void play(SkaldTrack track) {
    Log.i(TAG, "Music Played");
    //if(!isInitialized) {
    //  trackToPlay = track;
    //  return;
    //}

    Uri uri = track.getUri();
    String stringUri = uri.getPathSegments().get(uri.getPathSegments().size() - 1);
    spotifyPlayer.playUri(new com.spotify.sdk.android.player.Player.OperationCallback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Error error) {
                              Log.e(TAG, error.toString());
                            }
                          },
        stringUri, 0, 0);
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
  }

  @Override
  public void removePlayerReadyListener(OnPlayerReadyListener onPlayerReadyListener) {
    onPlayerReadyListeners.remove(onPlayerReadyListener);
  }
}
