package agency.tango.skald.spotify;

import agency.tango.skald.spotify.api.models.Tokens;
import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

class TokenService {
  private final SpotifyTokenApi spotifyTokenApi;

  TokenService() {
    this.spotifyTokenApi = resolveApi();
  }

  private SpotifyTokenApi resolveApi() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(SpotifyTokenApi.BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    return retrofit.create(SpotifyTokenApi.class);
  }

  Single<Tokens> getTokens(String clientId, String clientSecret, String code,
      String redirectUri) {
    return spotifyTokenApi.getTokens(clientId, clientSecret, "authorization_code", code, redirectUri);
  }

  Single<Tokens> getRefreshToken(String clientId, String clientSecret, String refreshToken) {
    return spotifyTokenApi.getRefreshToken(clientId, clientSecret, "refresh_token", refreshToken);
  }
}
