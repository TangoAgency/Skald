package agency.tango.skald.deezer.exceptions;

import agency.tango.skald.core.errors.AuthError;
import agency.tango.skald.core.exceptions.AuthException;

public class DeezerAuthException extends AuthException {
  public DeezerAuthException(AuthError authError) {
    super(authError);
  }

  public DeezerAuthException(String message, AuthError authError) {
    super(message, authError);
  }
}
