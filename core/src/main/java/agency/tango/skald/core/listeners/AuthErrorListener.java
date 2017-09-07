package agency.tango.skald.core.listeners;

import agency.tango.skald.core.AuthError;

public interface AuthErrorListener {
  void onAuthError(AuthError authorizationError);
}
