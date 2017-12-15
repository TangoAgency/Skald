package agency.tango.skald.spotify.exceptions;

import com.spotify.sdk.android.player.Error;

public class SpotifyException extends Exception {
  private final Error error;

  public SpotifyException(Error error) {
    this.error = error;
  }

  public Error getError() {
    return error;
  }
}
