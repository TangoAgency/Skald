package agency.tango.skald.spotify;

import agency.tango.skald.core.SkaldAuthData;

public class SpotifyAuthData extends SkaldAuthData{
  private final String oauthToken;

  public SpotifyAuthData(String oauthToken) {
    this.oauthToken = oauthToken;
  }
}
