package agency.tango.skald.spotify.services;

import agency.tango.skald.spotify.api.SpotifyTokenApi;
import agency.tango.skald.spotify.api.models.Tokens;
import io.reactivex.Single;

public class TokenService {
  private final SpotifyTokenApi spotifyTokenApi;

  public TokenService() {
    this.spotifyTokenApi = new SpotifyTokenApi.Builder().build();
  }

  public Single<Tokens> getTokens(String clientId, String clientSecret, String code,
      String redirectUri) {
    return spotifyTokenApi.getTokens(clientId, clientSecret, "authorization_code", code,
        redirectUri);
  }

  public Single<Tokens> getRefreshToken(String clientId, String clientSecret, String refreshToken) {
    return spotifyTokenApi.getRefreshToken(clientId, clientSecret, "refresh_token", refreshToken);
  }
}
