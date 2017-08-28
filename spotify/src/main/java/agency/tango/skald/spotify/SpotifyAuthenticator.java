package agency.tango.skald.spotify;

import android.app.Activity;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import agency.tango.skald.core.Authenticator;

public class SpotifyAuthenticator implements Authenticator {
  public static final int SPOTIFY_REQUEST_CODE = 1337;
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

    AuthenticationClient.openLoginActivity(activity, SPOTIFY_REQUEST_CODE, request);
  }

  @Override
  public void logout() {
  }
}
