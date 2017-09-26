package agency.tango.skald.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import agency.tango.skald.R;
import agency.tango.skald.core.SkaldMusicService;
import agency.tango.skald.core.errors.AuthError;
import agency.tango.skald.core.errors.PlaybackError;
import agency.tango.skald.core.listeners.OnAuthErrorListener;
import agency.tango.skald.core.listeners.OnErrorListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPreparedListener;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.core.models.TrackMetadata;
import agency.tango.skald.deezer.DeezerProvider;
import agency.tango.skald.spotify.SpotifyProvider;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends Activity {
  private static final String TAG = MainActivity.class.getSimpleName();
  public static final String SPOTIFY_CLIENT_ID = "8c43f75741454312adbbbb9d5ac6cb5b";
  public static final String SPOTIFY_REDIRECT_URI = "spotify-example-marcin-first-app://callback";
  private static final String SPOTIFY_CLIENT_SECRET = "f4becaa46ff247e0b9d90d4ab853b2a9";
  private static final String DEEZER_CLIENT_ID = "250322";
  private static final int AUTHORIZATION_REQUEST_CODE = 1334;

  private SkaldMusicService skaldMusicService;
  private ImageButton resumePauseButton;
  private ImageButton stopButton;
  private ImageView trackImage;
  private TextView artistName;
  private TextView title;
  private ListView listView;
  private TracksAdapter tracksAdapter;
  private boolean isPlaying = false;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    listView = (ListView) findViewById(R.id.list_view_tracks);
    resumePauseButton = (ImageButton) findViewById(R.id.imageButton_play);
    stopButton = (ImageButton) findViewById(R.id.imageButton_stop);
    trackImage = (ImageView) findViewById(R.id.image_cover);
    artistName = (TextView) findViewById(R.id.text_artist);
    title = (TextView) findViewById(R.id.text_title);

    tracksAdapter = new TracksAdapter(getApplicationContext(), R.layout.row_layout);
    listView.setAdapter(tracksAdapter);

    final SpotifyProvider spotifyProvider = new SpotifyProvider(this, SPOTIFY_CLIENT_ID,
        SPOTIFY_REDIRECT_URI, SPOTIFY_CLIENT_SECRET);
    final DeezerProvider deezerProvider = new DeezerProvider(this, DEEZER_CLIENT_ID);

    skaldMusicService = new SkaldMusicService(this, spotifyProvider, deezerProvider);

    skaldMusicService.addOnErrorListener(new OnErrorListener() {
      @Override
      public void onError() {
        Log.e(TAG, "Error in SkaldMusicService occurred");
      }
    });
    skaldMusicService.addOnAuthErrorListener(new OnAuthErrorListener() {
      @Override
      public void onAuthError(AuthError authError) {
        startAuthActivity(authError);
      }
    });
    skaldMusicService.addOnPlaybackListener(new OnPlaybackListener() {
      @Override
      public void onPlayEvent(TrackMetadata trackMetadata) {
        Log.d(TAG, String.format("%s - %s", trackMetadata.getArtistsName(),
            trackMetadata.getTitle()));
        isPlaying = true;
        notifyViews(trackMetadata);
      }

      @Override
      public void onPauseEvent() {
        Log.d(TAG, "Pause Event");
        isPlaying = false;
        notifyResumePauseButton();
      }

      @Override
      public void onResumeEvent() {
        Log.d(TAG, "Resume Event");
        isPlaying = true;
        notifyResumePauseButton();
      }

      @Override
      public void onStopEvent() {
        Log.d(TAG, "Stop event");
        isPlaying = false;
      }

      @Override
      public void onError(PlaybackError playbackError) {
        Log.e(TAG, "Playback error occurred");
      }
    });
    skaldMusicService.addOnPreparedListener(new OnPreparedListener() {
      @Override
      public void onPrepared(final SkaldMusicService skaldMusicService) {
        resumePauseButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (isPlaying) {
              skaldMusicService.pause();
            } else {
              skaldMusicService.resume();
            }
          }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            skaldMusicService.stop();
          }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final SkaldTrack item = (SkaldTrack) parent.getItemAtPosition(position);
            skaldMusicService.setSource(item);
            skaldMusicService.play();
          }
        });
      }
    });

    skaldMusicService.prepare();
  }

  @Override
  protected void onStart() {
    super.onStart();

    skaldMusicService.searchTracks("rap")
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new DisposableSingleObserver<List<SkaldTrack>>() {
          @Override
          public void onSuccess(List<SkaldTrack> skaldTracks) {
            tracksAdapter.addAll(skaldTracks);
          }

          @Override
          public void onError(Throwable error) {
            Log.e(TAG, "Error occurred in observer during searching for tracks", error);
          }
        });
  }

  @Override
  protected void onDestroy() {
    skaldMusicService.release();
    super.onDestroy();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == AUTHORIZATION_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        Log.d(TAG, "Authentication completed");
        skaldMusicService.prepare();
      } else {
        Log.e(TAG, "Authentication went wrong");
      }
    }
  }

  private void startAuthActivity(AuthError authError) {
    if (authError.hasResolution()) {
      Intent intent = authError.getResolution();
      startActivityForResult(intent, AUTHORIZATION_REQUEST_CODE);
    }
  }

  private void notifyViews(TrackMetadata trackMetadata) {
    if (isPlaying) {
      Picasso
          .with(this)
          .load(trackMetadata.getImageUrl())
          .into(trackImage);
      artistName.setText(trackMetadata.getArtistsName());
      title.setText(trackMetadata.getTitle());
    }
  }

  private void notifyResumePauseButton() {
    if (isPlaying) {
      resumePauseButton.setImageResource(R.drawable.ic_action_pause);
    } else {
      resumePauseButton.setImageResource(R.drawable.ic_action_play);
    }
  }
}
