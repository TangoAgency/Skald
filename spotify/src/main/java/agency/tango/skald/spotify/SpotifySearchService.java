package agency.tango.skald.spotify;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import agency.tango.skald.core.SearchService;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.spotify.api.models.BrowsePlaylists;
import agency.tango.skald.spotify.api.models.Playlist;
import agency.tango.skald.spotify.api.models.Tokens;
import agency.tango.skald.spotify.api.models.Track;
import agency.tango.skald.spotify.api.models.TrackSearch;
import agency.tango.skald.spotify.models.SpotifyPlaylist;
import agency.tango.skald.spotify.models.SpotifyTrack;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

class SpotifySearchService implements SearchService {
  private static final String TAG = SpotifySearchService.class.getSimpleName();
  private static final int UNAUTHORIZED_ERROR_CODE = 401;
  private final SpotifyApi spotifyApi;
  private final SpotifyProvider spotifyProvider;
  private final SpotifyAuthData spotifyAuthData;
  private final Context context;

  private String token;

  SpotifySearchService(Context context, SpotifyAuthData spotifyAuthData,
      SpotifyProvider spotifyProvider) {
    token = spotifyAuthData.getOauthToken();
    this.context = context;
    this.spotifyAuthData = spotifyAuthData;
    this.spotifyProvider = spotifyProvider;
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

  @Override
  public Single<List<SkaldTrack>> searchForTracks(final String query) {
    return spotifyApi.getTracksForQuery(query, "track")
        .onErrorResumeNext(new Function<Throwable, SingleSource<? extends TrackSearch>>() {
          @Override
          public SingleSource<? extends TrackSearch> apply(Throwable throwable) throws Exception {
            if (isTokenExpired(throwable)) {
              return refreshToken()
                  .flatMap(new Function<Tokens, SingleSource<TrackSearch>>() {
                    @Override
                    public SingleSource<TrackSearch> apply(Tokens tokens) throws Exception {
                      saveTokens(tokens);

                      return spotifyApi.getTracksForQuery(query, "track");
                    }
                  });
            }

            return Single.just(new TrackSearch());
          }
        })
        .map(new Function<TrackSearch, List<SkaldTrack>>() {
          @Override
          public List<SkaldTrack> apply(TrackSearch searchTrack) throws Exception {
            return mapSpotifyTracksToSkaldTracks(searchTrack);
          }
        });
  }

  @Override
  public Single<List<SkaldPlaylist>> searchForPlaylists(final String query) {
    return spotifyApi.getPlaylistsForQuery(query, "playlist")
        .onErrorResumeNext(new Function<Throwable, SingleSource<? extends BrowsePlaylists>>() {
          @Override
          public SingleSource<? extends BrowsePlaylists> apply(Throwable throwable)
              throws Exception {
            if (isTokenExpired(throwable)) {
              return refreshToken()
                  .flatMap(new Function<Tokens, SingleSource<BrowsePlaylists>>() {
                    @Override
                    public SingleSource<BrowsePlaylists> apply(Tokens tokens) throws Exception {
                      saveTokens(tokens);

                      return spotifyApi.getPlaylistsForQuery(query, "playlist");
                    }
                  });
            }

            return Single.just(new BrowsePlaylists());
          }
        })
        .map(new Function<BrowsePlaylists, List<SkaldPlaylist>>() {
          @Override
          public List<SkaldPlaylist> apply(BrowsePlaylists browsePlaylists) throws Exception {
            return mapSpotifyPlaylistsToSkaldPlaylists(browsePlaylists);
          }
        });
  }

  private boolean isTokenExpired(Throwable throwable) {
    return throwable instanceof HttpException
        && ((HttpException) throwable).code() == UNAUTHORIZED_ERROR_CODE;
  }

  private Single<Tokens> refreshToken() {
    return new TokenService()
        .getRefreshToken(spotifyProvider.getClientId(), spotifyProvider.getClientSecret(),
            spotifyAuthData.getRefreshToken());
  }

  private void saveTokens(Tokens tokens) {
    token = tokens.getAccessToken();

    SpotifyAuthData spotifyAuthDataRestored = new SpotifyAuthData(token,
        spotifyAuthData.getRefreshToken(), tokens.getExpiresIn());
    new SpotifyAuthStore(spotifyProvider).save(context, spotifyAuthDataRestored);
  }

  @NonNull
  private List<SkaldTrack> mapSpotifyTracksToSkaldTracks(TrackSearch searchTrack) {
    List<SkaldTrack> skaldTracks = new ArrayList<>();
    for (Track track : searchTrack.getTracks().getItems()) {
      skaldTracks.add(new SpotifyTrack(track));
    }
    return skaldTracks;
  }

  @NonNull
  private List<SkaldPlaylist> mapSpotifyPlaylistsToSkaldPlaylists(BrowsePlaylists browsePlaylists) {
    List<SkaldPlaylist> skaldPlaylists = new ArrayList<>();
    for (Playlist playlist : browsePlaylists.getPlaylists().getItems()) {
      skaldPlaylists.add(new SpotifyPlaylist(playlist));
    }
    return skaldPlaylists;
  }
}
