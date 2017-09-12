package agency.tango.skald.core;

import agency.tango.skald.core.factories.PlayerFactory;
import agency.tango.skald.core.factories.SearchServiceFactory;
import agency.tango.skald.core.factories.SkaldAuthStoreFactory;

public abstract class Provider {
  public abstract String getProviderName();

  public abstract PlayerFactory getPlayerFactory();

  public abstract SkaldAuthStoreFactory getSkaldAuthStoreFactory();

  public abstract SearchServiceFactory getSearchServiceFactory();
}
