package agency.tango.skald.deezer;

import android.content.Context;

import agency.tango.skald.core.AuthException;
import agency.tango.skald.core.Player;
import agency.tango.skald.core.Provider;
import agency.tango.skald.core.SearchService;
import agency.tango.skald.core.SkaldAuthStore;
import agency.tango.skald.core.factories.PlayerFactory;
import agency.tango.skald.core.factories.SearchServiceFactory;
import agency.tango.skald.core.factories.SkaldAuthStoreFactory;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.deezer.models.DeezerPlaylist;
import agency.tango.skald.deezer.models.DeezerTrack;

public class DeezerProvider extends Provider {
  static final String EXTRA_CLIENT_ID = "DEEZER_CLIENT_ID";

  private final Context context;
  private final String clientId;

  public DeezerProvider(Context context, String clientId) {
    this.context = context;
    this.clientId = clientId;
  }

  @Override
  public String getProviderName() {
    return DEEZER_PROVIDER;
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
  public SearchServiceFactory getSearchServiceFactory() {
    return new DeezerSearchServiceFactory(context, this);
  }

  @Override
  public boolean canHandle(SkaldPlaylist skaldPlaylist) {
    return skaldPlaylist instanceof DeezerPlaylist;
  }

  @Override
  public boolean canHandle(SkaldTrack skaldTrack) {
    return skaldTrack instanceof DeezerTrack;
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

  private static class DeezerSearchServiceFactory extends SearchServiceFactory {
    private final Context context;
    private final SkaldAuthStore skaldAuthStore;

    private DeezerSearchServiceFactory(Context context, DeezerProvider deezerProvider) {
      this.context = context;
      skaldAuthStore = new DeezerAuthStore(deezerProvider);
    }

    @Override
    public SearchService getSearchService() throws AuthException {
      DeezerAuthData deezerAuthData = (DeezerAuthData) skaldAuthStore.restore(context);
      return new DeezerSearchService(deezerAuthData.getDeezerConnect());
    }
  }
}
