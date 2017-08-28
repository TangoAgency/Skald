package agency.tango.skald.spotify.playerconfig;

import android.content.Intent;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import agency.tango.skald.core.PlayerConfig;

import static agency.tango.skald.spotify.SpotifyAuthenticator.SPOTIFY_REQUEST_CODE;

public class SpotifyPlayerConfigResolver {
  private static final String TAG = "SpotifyPlayerConfigRes";

  public PlayerConfig resolve(int requestCode, int resultCode, Intent data) {
    if (requestCode == SPOTIFY_REQUEST_CODE) {
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