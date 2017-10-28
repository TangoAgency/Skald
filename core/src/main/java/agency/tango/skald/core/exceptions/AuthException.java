package agency.tango.skald.core.exceptions;

import agency.tango.skald.core.errors.AuthError;

public abstract class AuthException extends Exception {
  private AuthError authError;

  public AuthException(AuthError authError) {
    this.authError = authError;
  }

  public AuthException(String message, AuthError authError) {
    super(message);
    this.authError = authError;
  }

  public AuthError getAuthError() {
    return authError;
  }
}
