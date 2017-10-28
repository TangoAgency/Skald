package agency.tango.skald.core.factories;

import agency.tango.skald.core.authentication.SkaldAuthStore;

public abstract class SkaldAuthStoreFactory {
  public abstract SkaldAuthStore getSkaldAuthStore();
}
