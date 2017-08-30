package agency.tango.skald.spotify;

import android.app.Activity;
import android.content.Intent;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class SpotifyAuthenticator {
  private static final int SPOTIFY_REQUEST_CODE = 1337;
  private static final String TAG = SpotifyAuthenticator.class.getSimpleName();
  private final String clientID;
  private final String redirectUri;

  public SpotifyAuthenticator(String clientID, String redirectUri) {
    this.clientID = clientID;
    this.redirectUri = redirectUri;
  }

  public void login(Activity activity) {
    final AuthenticationRequest request = new AuthenticationRequest.Builder(clientID,
        AuthenticationResponse.Type.TOKEN, redirectUri)
        .setScopes(new String[] {
            "user-read-private",
            "playlist-read-private",
            "playlist-read",
            "streaming" })
        .build();

    AuthenticationClient.openLoginActivity(activity, SPOTIFY_REQUEST_CODE, request);
  }
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == SPOTIFY_REQUEST_CODE) {
      AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
      switch (response.getType()) {
        case TOKEN:
          //callback.configReady(new PlayerConfig(PlayerConfig.SPOTIFY_PROVIDER, response.getAccessToken()));
        case ERROR:
          //callback.onError(response.getError());
          //Log.e(TAG, String.format("Auth error: %s", response.getError()));
          break;
        default:
          //callback.onError(response.getType().name());
          //Log.e(TAG, String.format("Not handled response %s", response.getType()));
      }
    }
  }

  //public void initializePlayer(PlayerConfig playerConfig, String clientId, Context context) {
  //  Config spotifyConfig = new Config(context.getApplicationContext(), playerConfig.getOauthToken(),
  //      clientId);
  //
  //  Spotify.getPlayer(spotifyConfig, context.getApplicationContext(),
  //      new SpotifyPlayer.InitializationObserver() {
  //        @Override
  //        public void onInitialized(final SpotifyPlayer player) {
  //          spotifyPlayer = player;
  //          Log.i("SpotifyPlayer", "Player initialized");
  //          if (playerReadyListener == null) {
  //            Log.e("Player", "ADD PlayerReadyListener to initialize SpotifyPlayer");
  //          } else {
  //            player.addConnectionStateCallback(new SpotifyConnectionStateCallback() {
  //              @Override
  //              public void onLoggedIn() {
  //                playerReadyListener.onPlayerReady(SpotifySkaldPlayer.this);
  //              }
  //
  //              @Override
  //              public void onLoginFailed(Error error) {
  //                playerReadyListener.onError();
  //              }
  //            });
  //          }
  //        }
  //
  //        @Override
  //        public void onError(Throwable throwable) {
  //          Log.e("SpotifyPlayer", "Error during initialization of the player", throwable);
  //        }
  //      });
  //}
}
