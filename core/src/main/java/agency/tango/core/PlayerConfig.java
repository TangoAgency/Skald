package agency.tango.core;

public class PlayerConfig {
  public static final String SPOTIFY_PROVIDER = "spotify";

  private final String provider;
  private final String oauthToken;

  public PlayerConfig(String provider, String oauthToken) {
    this.provider = provider;
    this.oauthToken = oauthToken;
  }

  public String getProvider() {
    return provider;
  }

  public String getOauthToken() {
    return oauthToken;
  }
}