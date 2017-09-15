package agency.tango.skald.deezer;

import android.content.Context;

import agency.tango.skald.core.AuthException;
import agency.tango.skald.core.Player;
import agency.tango.skald.core.Provider;
import agency.tango.skald.core.SkaldAuthStore;
import agency.tango.skald.core.factories.PlayerFactory;
import agency.tango.skald.core.factories.SearchServiceFactory;
import agency.tango.skald.core.factories.SkaldAuthStoreFactory;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;

public class DeezerProvider extends Provider {
  static final String EXTRA_CLIENT_ID = "DEEZER_CLIENT_ID";
  static final String DEEZER_PROVIDER = "deezer";

  private final Context context;
  private final String clientId;

  public DeezerProvider(Context context, String clientId, String redirectUri) {
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
    return null;
  }

  @Override
  public SearchServiceFactory getSearchServiceFactory() {
    return null;
  }

  @Override
  public boolean canHandle(SkaldPlaylist skaldPlaylist) {
    return false;
  }

  @Override
  public boolean canHandle(SkaldTrack skaldTrack) {
    return false;
  }

  public String getClientId() {
    return clientId;
  }

  private class DeezerPlayerFactory extends PlayerFactory {
    private final Context context;
    private final SkaldAuthStore skaldAuthStore;
    private final DeezerProvider deezerProvider;

    private DeezerPlayerFactory(Context context, DeezerProvider deezerProvider) {
      this.context = context;
      this.skaldAuthStore = new DeezerAuthStore(deezerProvider);
      this.deezerProvider = deezerProvider;
    }

    @Override
    public Player getPlayer() throws AuthException {
      DeezerAuthData deezerAuthData = (DeezerAuthData) skaldAuthStore.restore(context);
      return new SkaldDeezerPlayer(context, deezerAuthData);
    }
  }
}
