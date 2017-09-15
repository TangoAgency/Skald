package agency.tango.skald.deezer;

import agency.tango.skald.core.AuthException;
import agency.tango.skald.core.errors.AuthError;

public class DeezerAuthException extends AuthException {
  public DeezerAuthException(AuthError authError) {
    super(authError);
  }

  public DeezerAuthException(String message, AuthError authError) {
    super(message, authError);
  }
}
