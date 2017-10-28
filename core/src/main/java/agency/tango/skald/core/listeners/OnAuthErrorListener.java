package agency.tango.skald.core.listeners;

import agency.tango.skald.core.errors.AuthError;

public interface OnAuthErrorListener {
  void onAuthError(AuthError authError);
}
