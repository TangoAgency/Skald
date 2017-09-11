package agency.tango.skald.spotify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import agency.tango.skald.core.ApiCalls;
import agency.tango.skald.core.SkaldAuthData;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.spotify.api.models.BrowsePlaylists;
import agency.tango.skald.spotify.api.models.Playlist;
import agency.tango.skald.spotify.api.models.Track;
import agency.tango.skald.spotify.api.models.TrackSearch;
import agency.tango.skald.spotify.models.SpotifyPlaylist;
import agency.tango.skald.spotify.models.SpotifyTrack;
import io.reactivex.Single;
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
  public Single<List<SkaldTrack>> searchForTracks(String query) {
    return spotifyAPI.getTracksForQuery(query, "track")
        .map(new Function<TrackSearch, List<SkaldTrack>>() {
          @Override
          public List<SkaldTrack> apply(TrackSearch searchTrack) throws Exception {
            List<SkaldTrack> skaldTracks = new ArrayList<>();
            for (Track track : searchTrack.getTracks().getItems()) {
              skaldTracks.add(new SpotifyTrack(track));
            }
            return skaldTracks;
          }
        });
  }

  @Override
  public Single<List<SkaldPlaylist>> searchForPlaylists(String query) {
    return spotifyAPI.getPlaylistsForQuery(query, "playlist")
        .map(new Function<BrowsePlaylists, List<SkaldPlaylist>>() {
          @Override
          public List<SkaldPlaylist> apply(BrowsePlaylists browsePlaylists) throws Exception {
            List<SkaldPlaylist> skaldPlaylists = new ArrayList<>();
            for (Playlist playlist : browsePlaylists.getPlaylists().getItems()) {
              skaldPlaylists.add(new SpotifyPlaylist(playlist));
            }
            return skaldPlaylists;
          }
        });
  }
}
