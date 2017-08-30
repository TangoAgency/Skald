package agency.tango.skald.spotify;

import agency.tango.skald.core.Provider;

public class SpotifyProvider extends Provider {
  public static final String SPOTIFY_PROVIDER = "spotify";
  private final String clientId;
  private final String redirectUri;

  public SpotifyProvider(String clientId, String redirectUri) {
    this.clientId = clientId;
    this.redirectUri = redirectUri;
  }

  @Override
  public String getProviderName() {
    return SPOTIFY_PROVIDER;
  }

  public String getClientId() {
    return clientId;
  }

  public String getRedirectUri() {
    return redirectUri;
  }
}
