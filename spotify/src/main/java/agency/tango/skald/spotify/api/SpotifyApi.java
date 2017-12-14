package agency.tango.skald.spotify.api;

import android.content.Context;
import android.support.annotation.NonNull;
import org.reactivestreams.Publisher;
import java.net.HttpURLConnection;
import agency.tango.skald.spotify.api.models.BrowseCategories;
import agency.tango.skald.spotify.api.models.BrowsePlaylists;
import agency.tango.skald.spotify.api.models.Category;
import agency.tango.skald.spotify.api.models.Playlists;
import agency.tango.skald.spotify.api.models.SpotifyUser;
import agency.tango.skald.spotify.api.models.Tokens;
import agency.tango.skald.spotify.api.models.TrackSearch;
import agency.tango.skald.spotify.authentication.SpotifyAuthData;
import agency.tango.skald.spotify.authentication.SpotifyAuthStore;
import agency.tango.skald.spotify.provider.SpotifyProvider;
import agency.tango.skald.spotify.services.TokenService;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpotifyApi {
  String BASE_URL = "https://api.spotify.com";

  @GET("/v1/me")
  Single<SpotifyUser> getSpotifyUser();

  @GET("/v1/me/playlists")
  Single<Playlists> getPlaylists();

  @GET("/v1/browse/categories")
  Single<BrowseCategories> getCategories();

  @GET("/v1/browse/categories/{id}")
  Single<Category> getCategory(@Path("id") String id);

  @GET("/v1/browse/categories/{id}/playlists")
  Single<BrowsePlaylists> getPlaylistsInCategory(@Path("id") String id);

  @GET("/v1/search")
  Single<TrackSearch> getTracksForQuery(@Query("q") String query, @Query("type") String type);

  @GET("/v1/search")
  Single<BrowsePlaylists> getPlaylistsForQuery(@Query("q") String query,
      @Query("type") String type);

  class SpotifyApiImpl implements SpotifyApi {
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String AUTHORIZATION_HEADER_VALUE = "Bearer %s";

    private final SpotifyApi spotifyApi;
    private final SpotifyProvider spotifyProvider;
    private final Context context;
    private final TokenService tokenService;
    private final SpotifyAuthStore spotifyAuthStore;

    private String token;
    private String refreshToken;

    public SpotifyApiImpl(Context context, SpotifyProvider spotifyProvider) {
      this.context = context;
      this.spotifyApi = resolveApi();
      this.spotifyProvider = spotifyProvider;
      this.tokenService = new TokenService();
      this.spotifyAuthStore = new SpotifyAuthStore(spotifyProvider);
    }

    private SpotifyApi resolveApi() {
      OkHttpClient okHttpClient = new OkHttpClient.Builder()
          .addInterceptor(chain -> {
            Request request = chain.request()
                .newBuilder()
                .header(AUTHORIZATION_HEADER_NAME, String.format(AUTHORIZATION_HEADER_VALUE, token))
                .build();

            return chain.proceed(request);
          }).build();

      Retrofit retrofit = new Retrofit.Builder()
          .baseUrl(SpotifyApi.BASE_URL)
          .client(okHttpClient)
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .addConverterFactory(GsonConverterFactory.create())
          .build();

      return retrofit.create(SpotifyApi.class);
    }

    @Override
    public Single<SpotifyUser> getSpotifyUser() {
      return spotifyApi
          .getSpotifyUser()
          .retryWhen(isTokenExpired());
    }

    @Override
    public Single<Playlists> getPlaylists() {
      return spotifyApi
          .getPlaylists()
          .retryWhen(isTokenExpired());
    }

    @Override
    public Single<BrowseCategories> getCategories() {
      return spotifyApi
          .getCategories()
          .retryWhen(isTokenExpired());
    }

    @Override
    public Single<Category> getCategory(String id) {
      return spotifyApi
          .getCategory(id)
          .retryWhen(isTokenExpired());
    }

    @Override
    public Single<BrowsePlaylists> getPlaylistsInCategory(String id) {
      return spotifyApi
          .getPlaylistsInCategory(id)
          .retryWhen(isTokenExpired());
    }

    @Override
    public Single<TrackSearch> getTracksForQuery(String query, String type) {
      return spotifyApi
          .getTracksForQuery(query, type)
          .retryWhen(isTokenExpired());
    }

    @Override
    public Single<BrowsePlaylists> getPlaylistsForQuery(String query, String type) {
      return spotifyApi
          .getPlaylistsForQuery(query, type)
          .retryWhen(isTokenExpired());
    }

    public void setRefreshToken(String refreshToken) {
      this.refreshToken = refreshToken;
    }

    @NonNull
    private synchronized Function<Flowable<Throwable>, Publisher<Object>> isTokenExpired() {
      return errors -> errors.flatMap(error -> {
        if (isTokenExpired(error)) {
          return refreshToken()
              .toFlowable()
              .doOnNext(this::saveTokens);
        }
        return Flowable.error(error);
      });
    }

    private boolean isTokenExpired(Throwable throwable) {
      return throwable instanceof HttpException
          && ((HttpException) throwable).code() == HttpURLConnection.HTTP_UNAUTHORIZED;
    }

    private Single<Tokens> refreshToken() {
      return tokenService
          .getRefreshToken(spotifyProvider.getClientId(), spotifyProvider.getClientSecret(),
              refreshToken);
    }

    private void saveTokens(Tokens tokens) {
      token = tokens.getAccessToken();

      SpotifyAuthData spotifyAuthDataRestored = new SpotifyAuthData(token,
          refreshToken, tokens.getExpiresIn());
      spotifyAuthStore.save(context, spotifyAuthDataRestored);
    }
  }
}