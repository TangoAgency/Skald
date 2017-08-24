package agency.tango.spotify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;

import agency.tango.core.Authenticator;

public class SpotifyAuthenticator implements Authenticator {
  private final SpotifySkaldPlayer player;
  private String oauthToken;
  private final String clientID;
  private final String redirectUri;
  private static final int REQUEST_CODE = 1337;

  public SpotifyAuthenticator(SpotifySkaldPlayer player, String clientID, String redirectUri) {
    this.player = player;
    this.clientID = clientID;
    this.redirectUri = redirectUri;
  }

  public void login(Activity parentActivity) {
    final AuthenticationRequest request = new AuthenticationRequest.Builder(clientID,
        AuthenticationResponse.Type.TOKEN, redirectUri)
        .setScopes(new String[] { "user-read-private", "playlist-read-private",
            "playlist-read", "streaming" })
        .build();

    AuthenticationClient.openLoginActivity(parentActivity, REQUEST_CODE, request);
  }

  public void handleLoginResponse(Context context, int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_CODE) {
      AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
      switch (response.getType()) {
        case TOKEN:
          saveUserToken(response.getAccessToken());
          onAuthenticationComplete(context, response);
          break;
        case ERROR:
          Log.e("SpotifyLoginActivity", "Auth error: " + response.getError());
          break;
        default:
          Log.e("SpotifyLoginActivity", "Not handled response" + response.getType());
      }
    }
  }

  private void saveUserToken(String accessToken) {
    this.oauthToken = accessToken;
  }

  private void onAuthenticationComplete(Context context, AuthenticationResponse response) {
    String oauthToken = response.getAccessToken();
    Config playerConfig = new Config(context.getApplicationContext(), oauthToken, clientID);

    player.initializePlayer(playerConfig, context);
  }

  public void logout() {

  }
}
