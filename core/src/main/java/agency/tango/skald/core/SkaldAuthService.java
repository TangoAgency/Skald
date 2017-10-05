package agency.tango.skald.core;

import android.content.Context;

import agency.tango.skald.core.listeners.OnAuthErrorListener;

public class SkaldAuthService {
  private final Context context;
  private final OnAuthErrorListener onAuthErrorListener;

  public SkaldAuthService(Context context, OnAuthErrorListener onAuthErrorListener) {
    this.context = context;
    this.onAuthErrorListener = onAuthErrorListener;
  }

  public boolean login(Provider provider) {
    try {
      getSkaldAuthStore(provider).restore(context);
    } catch (AuthException authException) {
      if (authException.getAuthError().hasResolution()) {
        onAuthErrorListener.onAuthError(authException.getAuthError());
        return true;
      }
    }
    return false;
  }

  public void logout(Provider provider) {
    try {
      provider.getPlayerFactory().getPlayer().release();
      getSkaldAuthStore(provider).clear(context);
    } catch (AuthException authException) {
      if (authException.getAuthError().hasResolution()) {
        onAuthErrorListener.onAuthError(authException.getAuthError());
      }
    }
  }

  public boolean isLoggedIn(Provider provider) {
    try {
      getSkaldAuthStore(provider).restore(context);
    } catch (AuthException authException) {
      authException.printStackTrace();
      return false;
    }
    return true;
  }

  private SkaldAuthStore getSkaldAuthStore(Provider provider) {
    return provider.getSkaldAuthStoreFactory().getSkaldAuthStore();
  }
}
