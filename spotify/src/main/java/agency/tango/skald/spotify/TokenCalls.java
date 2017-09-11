package agency.tango.skald.spotify;

import agency.tango.skald.spotify.api.models.Tokens;
import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokenCalls {
  private final SpotifyAPI spotifyAPI;

  public TokenCalls(SpotifyAPI spotifyAPI) {
    this.spotifyAPI = resolveApi();
  }

  private SpotifyAPI resolveApi() {
    Retrofit retrofit = new Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    return retrofit.create(SpotifyAPI.class);
  }

  public Single<Tokens> getTokens(String clientId, String clientSecret, String code,
      String redirectUri) {
    return spotifyAPI.getTokens(clientId, clientSecret, "authorization_code", code, redirectUri);
  }
}
