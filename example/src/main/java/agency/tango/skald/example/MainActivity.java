package agency.tango.skald.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import agency.tango.skald.R;
import agency.tango.skald.core.errors.AuthError;
import agency.tango.skald.core.AuthException;
import agency.tango.skald.core.SkaldMusicService;
import agency.tango.skald.core.errors.PlaybackError;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPreparedListener;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.TrackMetadata;
import agency.tango.skald.spotify.SpotifyProvider;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends Activity {
  private static final String TAG = MainActivity.class.getSimpleName();
  public static final String SPOTIFY_CLIENT_ID = "8c43f75741454312adbbbb9d5ac6cb5b";
  public static final String SPOTIFY_REDIRECT_URI = "spotify-example-marcin-first-app://callback";
  private static final String SPOTIFY_CLIENT_SECRET = "f4becaa46ff247e0b9d90d4ab853b2a9";
  private SkaldMusicService skaldMusicService;
  private ListView listView;
  private Button pauseButton;
  private Button resumeButton;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    listView = (ListView) findViewById(R.id.list_view_playlists);
    pauseButton = (Button) findViewById(R.id.button_pause);
    resumeButton = (Button) findViewById(R.id.button_resume);

    pauseButton.setEnabled(false);
    resumeButton.setEnabled(false);

    SpotifyProvider spotifyProvider = new SpotifyProvider(this, SPOTIFY_CLIENT_ID,
        SPOTIFY_REDIRECT_URI, SPOTIFY_CLIENT_SECRET);

    skaldMusicService = new SkaldMusicService(this, spotifyProvider);

    try {
      skaldMusicService.prepare();
    } catch (AuthException authException) {
      AuthError authError = authException.getAuthError();
      if (authError.hasResolution()) {
        Intent intent = authError.getResolution();
        startActivity(intent);
      }
    }

    skaldMusicService.addOnPreparedListener(new OnPreparedListener() {
      @Override
      public void onPrepared(SkaldMusicService skaldMusicService) {
        skaldMusicService.addOnPlaybackListener(new OnPlaybackListener() {
          @Override
          public void onPlayEvent(TrackMetadata trackMetadata) {
            Log.d(TAG, String.format("%s - %s", trackMetadata.getArtistsName(),
                trackMetadata.getTitle()));
          }

          @Override
          public void onPauseEvent() {
            Log.d(TAG, "Pause Event");
          }

          @Override
          public void onResumeEvent() {
            Log.d(TAG, "Resume Event");
          }

          @Override
          public void onStopEvent() {

          }

          @Override
          public void onError(PlaybackError playbackError) {
            Log.e(TAG, "Playback error occurred");
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
        resumeButton.setEnabled(false);
        pauseButton.setEnabled(true);
      }
    });

    pauseButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        skaldMusicService.stop();
        resumeButton.setEnabled(true);
        pauseButton.setEnabled(false);
      }
    });

    resumeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        skaldMusicService.resume();
        pauseButton.setEnabled(true);
        resumeButton.setEnabled(false);
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();

    try {
      skaldMusicService.searchPlayList("hip-hop")
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new DisposableSingleObserver<List<SkaldPlaylist>>() {
            @Override
            public void onSuccess(List<SkaldPlaylist> skaldPlaylists) {
              Log.d(TAG, "Get Hip-hop playlist success");

              listView.setAdapter(new ArrayAdapter<>(MainActivity.this,
                  android.R.layout.simple_list_item_1, skaldPlaylists));
            }

            @Override
            public void onError(Throwable error) {
              Log.e(TAG, "Observer error", error);
            }
          });
    } catch (AuthException authException) {
      authException.printStackTrace();
    }
  }

  @Override
  protected void onDestroy() {
    skaldMusicService.release();
    super.onDestroy();
  }
}
