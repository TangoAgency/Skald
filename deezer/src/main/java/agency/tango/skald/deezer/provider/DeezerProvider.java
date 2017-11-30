package agency.tango.skald.deezer.provider;

import android.content.Context;

import agency.tango.skald.core.Player;
import agency.tango.skald.core.SearchService;
import agency.tango.skald.core.UserService;
import agency.tango.skald.core.authentication.SkaldAuthStore;
import agency.tango.skald.core.exceptions.AuthException;
import agency.tango.skald.core.factories.PlayerFactory;
import agency.tango.skald.core.factories.ServicesFactory;
import agency.tango.skald.core.factories.SkaldAuthStoreFactory;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.provider.Provider;
import agency.tango.skald.core.provider.ProviderName;
import agency.tango.skald.deezer.authentication.DeezerAuthData;
import agency.tango.skald.deezer.authentication.DeezerAuthStore;
import agency.tango.skald.deezer.models.DeezerPlaylist;
import agency.tango.skald.deezer.models.DeezerTrack;
import agency.tango.skald.deezer.player.SkaldDeezerPlayer;
import agency.tango.skald.deezer.services.DeezerSearchService;
import agency.tango.skald.deezer.services.DeezerUserService;

public class DeezerProvider extends Provider {
  public static final ProviderName NAME = new DeezerProviderName();
  public static final String EXTRA_CLIENT_ID = "DEEZER_CLIENT_ID";

  private final Context context;
  private final String clientId;

  public DeezerProvider(Context context, String clientId) {
    this.context = context;
    this.clientId = clientId;
  }

  @Override
  public ProviderName getProviderName() {
    return NAME;
  }

  @Override
  public PlayerFactory getPlayerFactory() {
    return new DeezerPlayerFactory(context, this);
  }

  @Override
  public SkaldAuthStoreFactory getSkaldAuthStoreFactory() {
    return new DeezerAuthStoreFactory(this);
  }

  @Override
  public ServicesFactory getServicesFactory() throws AuthException {
    SkaldAuthStore skaldAuthStore = new DeezerAuthStore(this);
    DeezerAuthData deezerAuthData = (DeezerAuthData) skaldAuthStore.restore(context);
    return new DeezerServicesFactory(deezerAuthData);
  }

  @Override
  public boolean canHandle(SkaldPlayableEntity skaldPlayableEntity) {
    return skaldPlayableEntity instanceof DeezerTrack
        || skaldPlayableEntity instanceof DeezerPlaylist;
  }

  public String getClientId() {
    return clientId;
  }

  private static class DeezerPlayerFactory extends PlayerFactory {
    private final Context context;
    private final SkaldAuthStore skaldAuthStore;

    private DeezerPlayerFactory(Context context, DeezerProvider deezerProvider) {
      this.context = context;
      this.skaldAuthStore = new DeezerAuthStore(deezerProvider);
    }

    @Override
    public Player getPlayer() throws AuthException {
      DeezerAuthData deezerAuthData = (DeezerAuthData) skaldAuthStore.restore(context);
      return new SkaldDeezerPlayer(context, deezerAuthData);
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

  private static class DeezerServicesFactory extends ServicesFactory {
    private final DeezerAuthData deezerAuthData;

    private DeezerServicesFactory(DeezerAuthData deezerAuthData) {
      this.deezerAuthData = deezerAuthData;
    }

    @Override
    public SearchService getSearchService() {
      return new DeezerSearchService(deezerAuthData.getDeezerConnect());
    }

    @Override
    public UserService getUserService() {
      return new DeezerUserService(deezerAuthData.getDeezerConnect());
    }
  }
}
