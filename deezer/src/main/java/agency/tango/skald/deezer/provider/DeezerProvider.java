package agency.tango.skald.deezer.provider;

import android.content.Context;
import agency.tango.skald.core.Player;
import agency.tango.skald.core.SearchService;
import agency.tango.skald.core.UserService;
import agency.tango.skald.core.authentication.SkaldAuthStore;
import agency.tango.skald.core.exceptions.AuthException;
import agency.tango.skald.core.factories.PlayerFactory;
import agency.tango.skald.core.factories.SearchServiceFactory;
import agency.tango.skald.core.factories.SkaldAuthStoreFactory;
import agency.tango.skald.core.factories.UserServiceFactory;
import agency.tango.skald.core.listeners.OnErrorListener;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.provider.Provider;
import agency.tango.skald.core.provider.ProviderName;
import agency.tango.skald.core.provider.UriValidator;
import agency.tango.skald.deezer.authentication.DeezerAuthData;
import agency.tango.skald.deezer.authentication.DeezerAuthStore;
import agency.tango.skald.deezer.player.SkaldDeezerPlayer;
import agency.tango.skald.deezer.services.DeezerSearchService;
import agency.tango.skald.deezer.services.DeezerUserService;

public class DeezerProvider extends Provider {
  public static final ProviderName NAME = new DeezerProviderName();
  public static final String EXTRA_CLIENT_ID = "DEEZER_CLIENT_ID";

  private final Context context;
  private final String clientId;
  private final DeezerAuthStore deezerAuthStore;

  public DeezerProvider(Context context, String clientId) {
    this.context = context;
    this.clientId = clientId;
    deezerAuthStore = new DeezerAuthStore(this);
  }

  @Override
  public ProviderName getProviderName() {
    return NAME;
  }

  @Override
  public PlayerFactory getPlayerFactory() {
    return new DeezerPlayerFactory(context, deezerAuthStore);
  }

  @Override
  public SkaldAuthStoreFactory getSkaldAuthStoreFactory() {
    return new DeezerAuthStoreFactory(this);
  }

  @Override
  public SearchServiceFactory getSearchServiceFactory() {
    return new DeezerSearchServiceFactory(context, deezerAuthStore);
  }

  @Override
  public UserServiceFactory getUserServiceFactory() {
    return new DeezerUserServiceFactory(context, deezerAuthStore);
  }

  @Override
  public boolean canHandle(SkaldPlayableEntity skaldPlayableEntity) {
    return UriValidator.validate(skaldPlayableEntity, NAME.getName());
  }

  public String getClientId() {
    return clientId;
  }

  private static class DeezerPlayerFactory extends PlayerFactory {
    private final Context context;
    private final DeezerAuthStore deezerAuthStore;

    private DeezerPlayerFactory(Context context, DeezerAuthStore deezerAuthStore) {
      this.context = context;
      this.deezerAuthStore = deezerAuthStore;
    }

    @Override
    public Player getPlayer(OnErrorListener onErrorListener) throws AuthException {
      DeezerAuthData deezerAuthData = (DeezerAuthData) deezerAuthStore.restore(context);
      return new SkaldDeezerPlayer(context, deezerAuthData, onErrorListener);
    }
  }

  private static class DeezerAuthStoreFactory extends SkaldAuthStoreFactory {
    private final DeezerProvider deezerProvider;

    private DeezerAuthStoreFactory(DeezerProvider deezerProvider) {
      this.deezerProvider = deezerProvider;
    }

    @Override
    public SkaldAuthStore getSkaldAuthStore() {
      return new DeezerAuthStore(deezerProvider);
    }
  }

  private static class DeezerSearchServiceFactory extends SearchServiceFactory {
    private final Context context;
    private final DeezerAuthStore deezerAuthStore;

    private DeezerSearchServiceFactory(Context context, DeezerAuthStore deezerAuthStore) {
      this.context = context;
      this.deezerAuthStore = deezerAuthStore;
    }

    @Override
    public SearchService getSearchService() throws AuthException {
      DeezerAuthData deezerAuthData = (DeezerAuthData) deezerAuthStore.restore(context);
      return new DeezerSearchService(deezerAuthData.getDeezerConnect());
    }
  }

  private static class DeezerUserServiceFactory extends UserServiceFactory {
    private final Context context;
    private final DeezerAuthStore deezerAuthStore;

    private DeezerUserServiceFactory(Context context, DeezerAuthStore deezerAuthStore) {
      this.context = context;
      this.deezerAuthStore = deezerAuthStore;
    }

    @Override
    public UserService getUserService() throws AuthException {
      DeezerAuthData deezerAuthData = (DeezerAuthData) deezerAuthStore.restore(context);
      return new DeezerUserService(deezerAuthData.getDeezerConnect());
    }
  }
}
