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
import io.reactivex.observers.DisposableSingleObserver;
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
        if (authError.hasResolution()) {
          Intent intent = authError.getResolution();
          startActivity(intent);
        }
      }
    });

    skaldMusicService.addOnPreparedListener(new OnPreparedListener() {
      @Override
      public void onPrepared(final SkaldMusicService skaldMusicService) {
        Log.d(TAG, "Inside onPreparedList");

        skaldMusicService.searchTrack("Desiigner")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new DisposableSingleObserver<List<SkaldTrack>>() {
              @Override
              public void onSuccess(List<SkaldTrack> skaldTracks) {
                skaldMusicService.setSource(skaldTracks.get(0));
                //skaldMusicService.playTrack();
              }

              @Override
              public void onError(Throwable error) {
                Log.e(TAG, "Observer error", error);
              }
            });

        skaldMusicService.searchPlayList("hip-hop")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new DisposableSingleObserver<List<SkaldPlaylist>>() {
              @Override
              public void onSuccess(List<SkaldPlaylist> skaldPlaylists) {
                listView.setAdapter(new ArrayAdapter<>(MainActivity.this,
                    android.R.layout.simple_list_item_1, skaldPlaylists));
              }

              @Override
              public void onError(Throwable error) {
                Log.e(TAG, "Observer error", error);
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

    findViewById(R.id.button_pause).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        skaldMusicService.pause();
      }
    });

    findViewById(R.id.button_resume).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        skaldMusicService.resume();
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
