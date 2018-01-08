package agency.tango.skald.core.provider;

import agency.tango.skald.core.factories.PlayerFactory;
import agency.tango.skald.core.factories.SearchServiceFactory;
import agency.tango.skald.core.factories.SkaldAuthStoreFactory;
import agency.tango.skald.core.factories.UserServiceFactory;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import io.reactivex.annotations.NonNull;

public abstract class Provider {
  public abstract ProviderName getProviderName();

  public abstract PlayerFactory getPlayerFactory();

  public abstract SkaldAuthStoreFactory getSkaldAuthStoreFactory();

  public abstract SearchServiceFactory getSearchServiceFactory();

  public abstract UserServiceFactory getUserServiceFactory();

  public abstract boolean canHandle(@NonNull SkaldPlayableEntity skaldPlayableEntity);
}
