package agency.tango.skald.core;

import android.content.Context;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import agency.tango.skald.core.listeners.OnAuthErrorListener;

import static agency.tango.skald.core.Provider.DEEZER_PROVIDER;
import static agency.tango.skald.core.Provider.SPOTIFY_PROVIDER;

public class SkaldAuthService {
  @Retention(RetentionPolicy.SOURCE)
  @StringDef({
      SPOTIFY_PROVIDER,
      DEEZER_PROVIDER
  })
  @interface ProviderName {}

  private final Context context;
  private final OnAuthErrorListener onAuthErrorListener;

  public SkaldAuthService(Context context, OnAuthErrorListener onAuthErrorListener) {
    this.context = context;
    this.onAuthErrorListener = onAuthErrorListener;
  }

  public boolean login(@ProviderName String providerName) {
    try {
      getSkaldAuthStore(Skald.singleton().getProviderByName(providerName)).restore(context);
    } catch (AuthException authException) {
      if (authException.getAuthError().hasResolution()) {
        onAuthErrorListener.onAuthError(authException.getAuthError());
        return true;
      }
    }
    return false;
  }

  public void logout(@ProviderName String providerName) {
    try {
      Skald.singleton().getProviderByName(providerName).getPlayerFactory().getPlayer().release();
      getSkaldAuthStore(Skald.singleton().getProviderByName(providerName)).clear(context);
    } catch (AuthException authException) {
      if (authException.getAuthError().hasResolution()) {
        onAuthErrorListener.onAuthError(authException.getAuthError());
      }
    }
  }

  public boolean isLoggedIn(@ProviderName String providerName) {
    try {
      getSkaldAuthStore(Skald.singleton().getProviderByName(providerName)).restore(context);
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
