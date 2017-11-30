package agency.tango.skald.core.provider;

import agency.tango.skald.core.exceptions.AuthException;
import agency.tango.skald.core.factories.PlayerFactory;
import agency.tango.skald.core.factories.ServicesFactory;
import agency.tango.skald.core.factories.SkaldAuthStoreFactory;
import agency.tango.skald.core.models.SkaldPlayableEntity;

public abstract class Provider {
  public abstract ProviderName getProviderName();

  public abstract PlayerFactory getPlayerFactory();

  public abstract SkaldAuthStoreFactory getSkaldAuthStoreFactory();

  public abstract ServicesFactory getServicesFactory() throws AuthException;

  public abstract boolean canHandle(SkaldPlayableEntity skaldPlayableEntity);
}
