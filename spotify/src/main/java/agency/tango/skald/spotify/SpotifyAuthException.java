package agency.tango.skald.spotify;

import agency.tango.skald.core.AuthError;
import agency.tango.skald.core.AuthException;

public class SpotifyAuthException extends AuthException{
  public SpotifyAuthException(AuthError authError) {
    super(authError);
  }

  public SpotifyAuthException(String message, AuthError authError) {
    super(message, authError);
  }
}
