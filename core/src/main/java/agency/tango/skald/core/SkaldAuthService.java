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

  public void login(Provider provider) {
    try {
      getSkaldAuthStore(provider).restore(context);
    } catch (AuthException authException) {
      if (authException.getAuthError().hasResolution()) {
        onAuthErrorListener.onAuthError(authException.getAuthError());
      }
    }
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

  private SkaldAuthStore getSkaldAuthStore(Provider provider) {
    return provider.getSkaldAuthStoreFactory().getSkaldAuthStore();
  }
}
