package agency.tango.skald.example;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import agency.tango.skald.R;
import agency.tango.skald.core.AuthError;
import agency.tango.skald.core.SkaldMusicService;
import agency.tango.skald.core.listeners.AuthErrorListener;
import agency.tango.skald.core.listeners.OnPreparedListener;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.spotify.SpotifyProvider;
import agency.tango.skald.spotify.models.SpotifyTrack;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends Activity {
  private static final String TAG = MainActivity.class.getSimpleName();
  public static final String SPOTIFY_CLIENT_ID = "8c43f75741454312adbbbb9d5ac6cb5b";
  public static final String SPOTIFY_REDIRECT_URI = "spotify-example-marcin-first-app://callback";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    final ListView listView = (ListView) findViewById(R.id.list_view_playlists);

    SpotifyProvider spotifyProvider = new SpotifyProvider(this, SPOTIFY_CLIENT_ID,
        SPOTIFY_REDIRECT_URI);

    final SkaldMusicService skaldMusicService = new SkaldMusicService(this, spotifyProvider);

    skaldMusicService.addAuthErrorListener(new AuthErrorListener() {
      @Override
      public void onAuthError(AuthError authError) {
        if(authError.hasResolution()) {
          Intent intent = authError.getResolution();
          startActivity(intent);
        }
      }
    });

    skaldMusicService.addOnPreparedListener(new OnPreparedListener() {
      @Override
      public void onPrepared(final SkaldMusicService skaldMusicService) {
        Log.d(TAG, "Inside onPreparedList");
        final List<SkaldTrack> skaldTracks = new ArrayList<>();
        final List<SkaldPlaylist> skaldPlaylists = new ArrayList<>();

        skaldMusicService.searchTrack("Desiigner")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new DisposableObserver<SkaldTrack>() {
              @Override
              public void onNext(SkaldTrack skaldTrack) {
                skaldTracks.add(skaldTrack);
              }

              @Override
              public void onError(Throwable error) {
                Log.e(TAG, "Observer error", error);
              }

              @Override
              public void onComplete() {
                //skaldMusicService.setSource(skaldTracks.get(0));
                //skaldMusicService.playTrack();
              }
            });

        skaldMusicService.searchPlayList("hip-hop")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new DisposableObserver<SkaldPlaylist>() {
              @Override
              public void onNext(SkaldPlaylist skaldPlaylist) {
                skaldPlaylists.add(skaldPlaylist);
              }

              @Override
              public void onError(Throwable error) {
                Log.e(TAG, "Observer error", error);
              }

              @Override
              public void onComplete() {
                listView.setAdapter(new ArrayAdapter<>(MainActivity.this,
                    android.R.layout.simple_list_item_1, skaldPlaylists));
                //skaldMusicService.setSource(skaldPlaylists.get(0));
                //skaldMusicService.playPlaylist();
              }
            });
      }
    });

    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final SkaldPlaylist item = (SkaldPlaylist) parent.getItemAtPosition(position);
        skaldMusicService.setSource(item);
        skaldMusicService.playPlaylist();
      }
    });

    Uri spotifyUri = Uri.parse(
        "skald://spotify/track/spotify:user:spotify:playlist:37i9dQZF1DX8vpLK1FoEw3");
    final SkaldTrack skaldTrack = new SpotifyTrack(spotifyUri);

    skaldMusicService.setSource(skaldTrack);
    skaldMusicService.prepare();


    //Player player = spotifyProvider
    //    .getPlayerFactory()
    //    .getPlayerFor(new SpotifyTrack(spotifyUri));
    //
    //player.playTrack(new SpotifyTrack(spotifyUri));

    //Button spotifyButton = (Button) findViewById(R.id.button_spotify);
    //spotifyButton.setOnClickListener(new View.OnClickListener() {
    //  @Override
    //  public void onClick(View v) {
    //    Intent intent = new Intent(MainActivity.this, SpotifyActivity.class);
    //    startActivity(intent);
    //  }
    //});
    //
    //Button deezerButton = (Button) findViewById(R.id.button_deezer);
    //deezerButton.setOnClickListener(new View.OnClickListener() {
    //  @Override
    //  public void onClick(View v) {
    //    Intent intent = new Intent(MainActivity.this, DeezerActivity.class);
    //    startActivity(intent);
    //  }
    //});
  }
}
