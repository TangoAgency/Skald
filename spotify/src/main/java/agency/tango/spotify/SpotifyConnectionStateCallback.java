package agency.tango.spotify;

import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;

public class SpotifyConnectionStateCallback implements ConnectionStateCallback {
  @Override
  public void onLoggedIn() {
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
}
