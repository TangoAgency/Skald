package agency.tango.skald.spotify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import agency.tango.skald.core.ApiCalls;
import agency.tango.skald.core.SkaldAuthData;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.spotify.api.models.Track;
import agency.tango.skald.spotify.api.models.Tracks;
import agency.tango.skald.spotify.models.SpotifyTrack;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SpotifyApiCalls implements ApiCalls {
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
  public List<SkaldTrack> searchForTracks(String query) {
    final List<SkaldTrack> spotifyTracks = new ArrayList<>();

    spotifyAPI.getTracksForQuery(query)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new DisposableSingleObserver<Tracks>() {
          @Override
          public void onSuccess(Tracks tracks) {
            for(Track track : tracks.getTracks()) {
              spotifyTracks.add(new SpotifyTrack(track));
            }
          }

          @Override
          public void onError(Throwable e) {

          }
        });

    return spotifyTracks;
  }
}
