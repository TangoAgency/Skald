package agency.tango.skald.spotify;

import agency.tango.skald.core.AuthError;
import agency.tango.skald.core.AuthException;

class SpotifyAuthException extends AuthException{
  public SpotifyAuthException(AuthError authError) {
    super(authError);
  }

  SpotifyAuthException(String message, AuthError authError) {
    super(message, authError);
  }
}
