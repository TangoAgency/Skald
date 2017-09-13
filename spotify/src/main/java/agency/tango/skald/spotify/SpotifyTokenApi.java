package agency.tango.skald.spotify;

import agency.tango.skald.spotify.api.models.Tokens;
import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SpotifyTokenApi {
  String BASE_URL = "https://accounts.spotify.com";

  @FormUrlEncoded
  @POST("/api/token")
  Single<Tokens> getTokens(@Field("client_id") String clientId,
      @Field("client_secret") String clientSecret, @Field("grant_type") String grantType,
      @Field("code") String code, @Field("redirect_uri") String redirectUri);

  @FormUrlEncoded
  @POST("/api/token")
  Single<Tokens> getRefreshToken(@Field("client_id") String clientId,
      @Field("client_secret") String clientSecret, @Field("grant_type") String grantType,
      @Field("refresh_token") String refreshToken);

  class Builder {
    public SpotifyTokenApi build() {
      Retrofit retrofit = new Retrofit.Builder()
          .baseUrl(SpotifyTokenApi.BASE_URL)
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .addConverterFactory(GsonConverterFactory.create())
          .build();

      return retrofit.create(SpotifyTokenApi.class);
    }
  }
}
