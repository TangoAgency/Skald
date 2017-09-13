package agency.tango.skald.spotify;

import agency.tango.skald.spotify.api.models.Tokens;
import io.reactivex.Single;

class TokenService {
  private final SpotifyTokenApi spotifyTokenApi;

  TokenService() {
    this.spotifyTokenApi = new SpotifyTokenApi.Builder().build();
  }

  Single<Tokens> getTokens(String clientId, String clientSecret, String code,
      String redirectUri) {
    return spotifyTokenApi.getTokens(clientId, clientSecret, "authorization_code", code,
        redirectUri);
  }

  Single<Tokens> getRefreshToken(String clientId, String clientSecret, String refreshToken) {
    return spotifyTokenApi.getRefreshToken(clientId, clientSecret, "refresh_token", refreshToken);
  }
}
