package agency.tango.skald.core;

import android.content.Context;

import agency.tango.skald.core.bus.LoginEvent;
import agency.tango.skald.core.bus.SkaldBus;
import agency.tango.skald.core.listeners.OnAuthErrorListener;

public class SkaldAuthService {
  private final Context context;
  private final OnAuthErrorListener onAuthErrorListener;
  private final SkaldBus skaldBus = SkaldBus.getInstance();

  public SkaldAuthService(Context context, OnAuthErrorListener onAuthErrorListener) {
    this.context = context;
    this.onAuthErrorListener = onAuthErrorListener;
  }

  public boolean login(ProviderName providerName) {
    try {
      getSkaldAuthStore(getProviderByName(providerName)).restore(context);
    } catch (AuthException authException) {
      if (authException.getAuthError().hasResolution()) {
        onAuthErrorListener.onAuthError(authException.getAuthError());
        return true;
      }
    }
    return false;
  }

  public void logout(ProviderName providerName) {
    getSkaldAuthStore(getProviderByName(providerName)).clear(context);
    skaldBus.post(new LoginEvent(providerName));
  }

  public boolean isLoggedIn(ProviderName providerName) {
    try {
      getSkaldAuthStore(getProviderByName(providerName)).restore(context);
    } catch (AuthException authException) {
      authException.printStackTrace();
      return false;
    }
    return true;
  }

  private Provider getProviderByName(ProviderName providerName) {
    return Skald.singleton().getProviderByName(providerName);
  }

  private SkaldAuthStore getSkaldAuthStore(Provider provider) {
    return provider.getSkaldAuthStoreFactory().getSkaldAuthStore();
  }
}
