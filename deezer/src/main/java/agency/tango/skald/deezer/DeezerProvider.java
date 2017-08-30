package agency.tango.skald.deezer;

import agency.tango.skald.core.Provider;

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

  public String getClientId() {
    return clientId;
  }
}
