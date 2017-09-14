package agency.tango.skald.deezer;

import agency.tango.skald.core.Provider;
import agency.tango.skald.core.factories.PlayerFactory;
import agency.tango.skald.core.factories.SearchServiceFactory;
import agency.tango.skald.core.factories.SkaldAuthStoreFactory;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;

public class DeezerProvider extends Provider {
  public static final String DEEZER_PROVIDER = "deezer";
  private final String clientId;

  public DeezerProvider(String clientId, String redirectUri) {
    this.clientId = clientId;
  }

  @Override
  public String getProviderName() {
    return DEEZER_PROVIDER;
  }

  @Override
  public PlayerFactory getPlayerFactory() {
    return null;
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
}
