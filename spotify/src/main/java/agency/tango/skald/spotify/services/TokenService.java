package agency.tango.skald.spotify.services;

import agency.tango.skald.spotify.api.SpotifyTokenApi;
import agency.tango.skald.spotify.api.models.Tokens;
import io.reactivex.Single;

public class TokenService {
  private static final String AUTHORIZATION_CODE_GRANT_TYPE = "authorization_code";
  private static final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";

  private final SpotifyTokenApi spotifyTokenApi;

  public TokenService() {
    this.spotifyTokenApi = new SpotifyTokenApi.Builder().build();
  }

  public Single<Tokens> getTokens(String clientId, String clientSecret, String code,
      String redirectUri) {
    return spotifyTokenApi.getTokens(clientId, clientSecret, AUTHORIZATION_CODE_GRANT_TYPE, code,
        redirectUri);
  }

  public Single<Tokens> getRefreshToken(String clientId, String clientSecret, String refreshToken) {
    return spotifyTokenApi.getRefreshToken(clientId, clientSecret, REFRESH_TOKEN_GRANT_TYPE,
        refreshToken);
  }
}
