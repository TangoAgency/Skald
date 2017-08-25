package agency.tango.core;

public class PlayerConfig {
  public static final String SPOTIFY_PROVIDER = "spotify";
  public static final String DEEZER_PROVIDER = "deezer";

  private String provider; // Spotify or Deezer or Youtube
  private String oauthToken;

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
