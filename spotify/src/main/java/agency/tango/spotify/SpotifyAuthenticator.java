package agency.tango.spotify;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import agency.tango.core.Authenticator;
import agency.tango.core.PlayerConfig;

public class SpotifyAuthenticator implements Authenticator {
  private static final String TAG = "SpotifyAuthenticator";
  private static final int REQUEST_CODE = 1337;

  private final String clientID;
  private final String redirectUri;

  public SpotifyAuthenticator(String clientID, String redirectUri) {
    this.clientID = clientID;
    this.redirectUri = redirectUri;
  }

  @Override
  public void login(Activity activity) {
    final AuthenticationRequest request = new AuthenticationRequest.Builder(clientID,
        AuthenticationResponse.Type.TOKEN, redirectUri)
        .setScopes(new String[] {
            "user-read-private",
            "playlist-read-private",
            "playlist-read",
            "streaming" })
        .build();

    AuthenticationClient.openLoginActivity(activity, REQUEST_CODE, request);
  }

  @Override
  public void logout() {
  }

  @Override
  public PlayerConfig retrievePlayerConfigFromLoginResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_CODE) {
      AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
      switch (response.getType()) {
        case TOKEN:
          return new PlayerConfig(PlayerConfig.SPOTIFY_PROVIDER, response.getAccessToken());
        case ERROR:
          Log.e(TAG, String.format("Auth error: %s", response.getError()));
          break;
        default:
          Log.e(TAG, String.format("Not handled response %s", response.getType()));
      }
    }
    return new PlayerConfig(PlayerConfig.SPOTIFY_PROVIDER, "");
  }
}
