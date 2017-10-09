package agency.tango.skald.core;

import android.util.Log;

import agency.tango.skald.core.bus.LoginEvent;
import agency.tango.skald.core.bus.SkaldEvent;
import agency.tango.skald.core.cache.TLruCache;
import agency.tango.skald.core.provider.ProviderName;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

public class LoginEventObserver extends DisposableObserver<SkaldEvent> {
  private static final String TAG = LoginEventObserver.class.getSimpleName();
  private final TLruCache<ProviderName, Player> playerCache;

  public LoginEventObserver(TLruCache<ProviderName, Player> playerCache) {
    this.playerCache = playerCache;
  }

  @Override
  public void onNext(@NonNull SkaldEvent skaldEvent) {
    if (skaldEvent instanceof LoginEvent) {
      playerCache.remove(((LoginEvent) skaldEvent).getProviderName());
    }
  }

  @Override
  public void onError(@NonNull Throwable e) {
    Log.e(TAG, "LoginEventObserver error", e);
  }

  @Override
  public void onComplete() {

  }
}
