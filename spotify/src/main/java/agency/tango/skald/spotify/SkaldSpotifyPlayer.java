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

import agency.tango.skald.core.Player;
import agency.tango.skald.core.models.SkaldTrack;

public class SkaldSpotifyPlayer implements Player {
  private static final String TAG = SkaldSpotifyPlayer.class.getSimpleName();
  private SpotifyPlayer spotifyPlayer;

  private boolean isInitialized = false;
  private SkaldTrack trackToPlay = null;

  public SkaldSpotifyPlayer(Context context, String clientId) {
    String oauthToken = "";
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
                isInitialized = true;
                if(trackToPlay != null) {
                  play(trackToPlay);
                  trackToPlay = null;
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
    if(!isInitialized) {
      trackToPlay = track;
      return;
    }

    Uri uri = track.getUri();
    String uri1 = uri.getPathSegments().get(uri.getPathSegments().size() - 1);
    Log.d(TAG, String.format("URLL %s", uri1));
    spotifyPlayer.playUri(new com.spotify.sdk.android.player.Player.OperationCallback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Error error) {
                              Log.e(TAG, error.toString());
                            }
                          },
        uri1, 0, 0);
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
}
