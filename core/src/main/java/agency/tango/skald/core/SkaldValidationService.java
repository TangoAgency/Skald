package agency.tango.skald.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static agency.tango.skald.core.SkaldMusicService.EXTRA_AUTH_DATA;
import static agency.tango.skald.core.SkaldMusicService.EXTRA_PROVIDER_NAME;
import static agency.tango.skald.core.SkaldMusicService.INTENT_ACTION;

public class SkaldValidationService {
  private final Context context;
  private final List<Provider> providers = new ArrayList<>();

  public SkaldValidationService(Context context, Provider... providers) {
    this.context = context;
    this.providers.addAll(Arrays.asList(providers));

    BroadcastReceiver messageReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        SkaldAuthData skaldAuthData = intent.getExtras().getParcelable(EXTRA_AUTH_DATA);
        String providerName = intent.getStringExtra(EXTRA_PROVIDER_NAME);
        for (Provider provider : SkaldValidationService.this.providers) {
          if (provider.getProviderName().equals(providerName)) {
            getSkaldAuthStore(provider).save(context, skaldAuthData);
          }
        }
      }
    };

    LocalBroadcastManager
        .getInstance(context.getApplicationContext())
        .registerReceiver(messageReceiver, new IntentFilter(INTENT_ACTION));
  }

  public boolean isProviderValid(Provider provider) {
    try {
      getSkaldAuthStore(provider).restore(context);
      return true;
    } catch (AuthException authException) {
      return false;
    }
  }

  public Intent login(Provider provider) {
    try {
      getSkaldAuthStore(provider).restore(context);
    } catch (AuthException authException) {
      if (authException.getAuthError().hasResolution()) {
        return authException.getAuthError().getResolution();
      }
    }
    return null;
  }

  public void logout(Provider provider) {
    try {
      provider.getPlayerFactory().getPlayer().release();
      //todo should clear auth cache
    } catch (AuthException authException) {
      //todo notify error
    }
  }

  private SkaldAuthStore getSkaldAuthStore(Provider provider) {
    return provider.getSkaldAuthStoreFactory().getSkaldAuthStore();
  }
}
