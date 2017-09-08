package agency.tango.skald.spotify;

import java.io.IOException;
import java.util.List;

import agency.tango.skald.core.ApiCalls;
import agency.tango.skald.core.SkaldAuthData;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.spotify.api.models.Track;
import agency.tango.skald.spotify.api.models.TrackSearch;
import agency.tango.skald.spotify.models.SpotifyTrack;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SpotifyApiCalls implements ApiCalls {
  private static final String TAG = SpotifyApiCalls.class.getSimpleName();
  private final SpotifyAPI spotifyAPI;

  public SpotifyApiCalls(SkaldAuthData skaldAuthData) {
    this.spotifyAPI = resolveApi(skaldAuthData);
  }

  private SpotifyAPI resolveApi(final SkaldAuthData skaldAuthData) {
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .addInterceptor(new Interceptor() {
          @Override
          public Response intercept(Chain chain) throws IOException {
            Request request = chain.request()
                .newBuilder()
                .header("Authorization", String.format("Bearer %s",
                    ((SpotifyAuthData) skaldAuthData).getOauthToken()))
                .build();

            return chain.proceed(request);
          }
        }).build();

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(SpotifyAPI.BASE_URL)
        .client(okHttpClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    return retrofit.create(SpotifyAPI.class);
  }

  @Override
  public Observable<SkaldTrack> searchForTracks(String query) {
    return spotifyAPI.getTracksForQuery(query, "track")
        .toObservable()
        .map(new Function<TrackSearch, List<Track>>() {
          @Override
          public List<Track> apply(TrackSearch searchTrack) throws Exception {
            return searchTrack.getTracks().getItems();
          }
        })
        .flatMapIterable(new Function<List<Track>, Iterable<? extends Track>>() {
          @Override
          public Iterable<? extends Track> apply(List<Track> item) throws Exception {
            return item;
          }
        })
        .map(new Function<Track, SkaldTrack>() {
          @Override
          public SkaldTrack apply(Track track) throws Exception {
            return new SpotifyTrack(track);
          }
        });
  }
}
