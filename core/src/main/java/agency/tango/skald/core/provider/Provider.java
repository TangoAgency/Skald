package agency.tango.skald.core.provider;

import android.support.annotation.NonNull;
import agency.tango.skald.core.factories.PlayerFactory;
import agency.tango.skald.core.factories.SearchServiceFactory;
import agency.tango.skald.core.factories.SkaldAuthStoreFactory;
import agency.tango.skald.core.factories.UserServiceFactory;
import agency.tango.skald.core.models.SkaldPlayableEntity;

public abstract class Provider {
  @NonNull
  public abstract ProviderName getProviderName();

  @NonNull
  public abstract PlayerFactory getPlayerFactory();

  @NonNull
  public abstract SkaldAuthStoreFactory getSkaldAuthStoreFactory();

  @NonNull
  public abstract SearchServiceFactory getSearchServiceFactory();

  @NonNull
  public abstract UserServiceFactory getUserServiceFactory();

  public abstract boolean canHandle(@NonNull SkaldPlayableEntity skaldPlayableEntity);
}
