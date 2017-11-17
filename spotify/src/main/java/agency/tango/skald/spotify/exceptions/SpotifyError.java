package agency.tango.skald.spotify.exceptions;

import com.spotify.sdk.android.player.Error;

public class SpotifyError extends Exception {
  private final Error error;

  public SpotifyError(Error error) {
    this.error = error;
  }

  public Error getError() {
    return error;
  }
}
