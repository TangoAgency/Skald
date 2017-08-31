package agency.tango.skald.spotify;

import agency.tango.skald.core.SkaldAuthorizationData;

public class SpotifyAuthorizationData extends SkaldAuthorizationData {
  private final String oauthToken;

  public SpotifyAuthorizationData(String oauthToken) {
    this.oauthToken = oauthToken;
  }

  public String getOauthToken() {
    return oauthToken;
  }
}
