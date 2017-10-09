package agency.tango.skald.core;

import android.util.Log;

import agency.tango.skald.core.bus.LoginEvent;
import agency.tango.skald.core.bus.SkaldEvent;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

public class SkaldEventObserver extends DisposableObserver<SkaldEvent> {
  private static final String TAG = SkaldEventObserver.class.getSimpleName();
  private final TLruCache<ProviderName, Player> playerCache;

  public SkaldEventObserver(TLruCache<ProviderName, Player> playerCache) {
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
    Log.e(TAG, "SkaldEventObserver error", e);
  }

  @Override
  public void onComplete() {

  }
}
