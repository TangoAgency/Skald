package agency.tango.skald.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import static agency.tango.skald.core.SkaldMusicService.EXTRA_AUTH_DATA;
import static agency.tango.skald.core.SkaldMusicService.EXTRA_PROVIDER_NAME;

public class AuthDataReceiver extends BroadcastReceiver {
  private final List<Provider> providers;

  AuthDataReceiver(final List<Provider> providers) {
    this.providers = providers;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    SkaldAuthData skaldAuthData = intent.getExtras().getParcelable(EXTRA_AUTH_DATA);
    String providerName = intent.getStringExtra(EXTRA_PROVIDER_NAME);
    for (Provider provider : providers) {
      if (provider.getProviderName().getName().equals(providerName)) {
        getSkaldAuthStore(provider).save(context, skaldAuthData);
      }
    }
  }

  private SkaldAuthStore getSkaldAuthStore(Provider provider) {
    return provider.getSkaldAuthStoreFactory().getSkaldAuthStore();
  }
}