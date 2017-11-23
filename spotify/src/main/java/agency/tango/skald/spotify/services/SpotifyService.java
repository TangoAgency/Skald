package agency.tango.skald.spotify.services;

import android.content.Context;

import java.io.IOException;

import agency.tango.skald.spotify.api.SpotifyApi;
import agency.tango.skald.spotify.api.models.Tokens;
import agency.tango.skald.spotify.authentication.SpotifyAuthData;
import agency.tango.skald.spotify.authentication.SpotifyAuthStore;
import agency.tango.skald.spotify.provider.SpotifyProvider;
import io.reactivex.Single;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

abstract class SpotifyService {
  private static final int UNAUTHORIZED_ERROR_CODE = 401;

  private String token;
  final SpotifyApi spotifyApi;
  private final SpotifyProvider spotifyProvider;
  private final SpotifyAuthData spotifyAuthData;
  private final Context context;
  private final TokenService tokenService;
  private final SpotifyAuthStore spotifyAuthStore;

  SpotifyService(Context context, SpotifyAuthData spotifyAuthData,
      SpotifyProvider spotifyProvider) {
    token = spotifyAuthData.getOauthToken();
    this.context = context;
    this.spotifyAuthData = spotifyAuthData;
    this.spotifyProvider = spotifyProvider;
    this.tokenService = new TokenService();
    this.spotifyAuthStore = new SpotifyAuthStore(spotifyProvider);
    this.spotifyApi = resolveApi();
  }

  private SpotifyApi resolveApi() {
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .addInterceptor(new Interceptor() {
          @Override
          public Response intercept(Chain chain) throws IOException {
            Request request = chain.request()
                .newBuilder()
                .header("Authorization", String.format("Bearer %s", token))
                .build();

            return chain.proceed(request);
          }
        }).build();

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(SpotifyApi.BASE_URL)
        .client(okHttpClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    return retrofit.create(SpotifyApi.class);
  }

  boolean isTokenExpired(Throwable throwable) {
    return throwable instanceof HttpException
        && ((HttpException) throwable).code() == UNAUTHORIZED_ERROR_CODE;
  }

  Single<Tokens> refreshToken() {
    return tokenService
        .getRefreshToken(spotifyProvider.getClientId(), spotifyProvider.getClientSecret(),
            spotifyAuthData.getRefreshToken());
  }

  void saveTokens(Tokens tokens) {
    token = tokens.getAccessToken();

    SpotifyAuthData spotifyAuthDataRestored = new SpotifyAuthData(token,
        spotifyAuthData.getRefreshToken(), tokens.getExpiresIn());
    spotifyAuthStore.save(context, spotifyAuthDataRestored);
  }
}
