package agency.tango.skald.spotify.exceptions;

import agency.tango.skald.core.errors.AuthError;
import agency.tango.skald.core.exceptions.AuthException;

public class SpotifyAuthException extends AuthException {
  public SpotifyAuthException(AuthError authError) {
    super(authError);
  }

  public SpotifyAuthException(String message, AuthError authError) {
    super(message, authError);
  }
}
