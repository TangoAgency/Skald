package agency.tango.skald.core;

import android.content.Context;
import android.support.annotation.NonNull;
import agency.tango.skald.core.authentication.SkaldAuthStore;
import agency.tango.skald.core.bus.LoginEvent;
import agency.tango.skald.core.bus.SkaldBus;
import agency.tango.skald.core.exceptions.AuthException;
import agency.tango.skald.core.listeners.OnAuthErrorListener;
import agency.tango.skald.core.provider.Provider;
import agency.tango.skald.core.provider.ProviderName;

public class SkaldAuthService {
  @NonNull
  private final Context context;

  @NonNull
  private final OnAuthErrorListener onAuthErrorListener;

  private final SkaldBus skaldBus = SkaldBus.getInstance();

  public SkaldAuthService(@NonNull Context context,
      @NonNull OnAuthErrorListener onAuthErrorListener) {
    this.context = context;
    this.onAuthErrorListener = onAuthErrorListener;
  }

  public boolean login(@NonNull ProviderName providerName) {
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

  public void logout(@NonNull ProviderName providerName) {
    getSkaldAuthStore(getProviderByName(providerName)).clear(context);
    skaldBus.post(new LoginEvent(providerName));
  }

  public boolean isLoggedIn(@NonNull ProviderName providerName) {
    try {
      getSkaldAuthStore(getProviderByName(providerName)).restore(context);
    } catch (AuthException authException) {
      return false;
    }
    return true;
  }

  private Provider getProviderByName(ProviderName providerName) {
    return Skald.instance().getProviderByName(providerName);
  }

  private SkaldAuthStore getSkaldAuthStore(Provider provider) {
    return provider.getSkaldAuthStoreFactory().getSkaldAuthStore();
  }
}
