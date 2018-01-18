package agency.tango.skald.core.factories;

import android.support.annotation.NonNull;
import agency.tango.skald.core.authentication.SkaldAuthStore;

public abstract class SkaldAuthStoreFactory {
  @NonNull
  public abstract SkaldAuthStore getSkaldAuthStore();
}
