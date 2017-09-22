package agency.tango.skald.example;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import agency.tango.skald.R;
import agency.tango.skald.core.SkaldMusicService;
import agency.tango.skald.core.errors.AuthError;
import agency.tango.skald.core.errors.PlaybackError;
import agency.tango.skald.core.listeners.OnAuthErrorListener;
import agency.tango.skald.core.listeners.OnErrorListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPreparedListener;
import agency.tango.skald.core.models.TrackMetadata;
import agency.tango.skald.deezer.DeezerProvider;
import agency.tango.skald.deezer.models.DeezerTrack;
import agency.tango.skald.spotify.SpotifyProvider;
import agency.tango.skald.spotify.models.SpotifyTrack;

public class MainActivity extends Activity {
  private static final String TAG = MainActivity.class.getSimpleName();
  public static final String SPOTIFY_CLIENT_ID = "8c43f75741454312adbbbb9d5ac6cb5b";
  public static final String SPOTIFY_REDIRECT_URI = "spotify-example-marcin-first-app://callback";
  private static final String SPOTIFY_CLIENT_SECRET = "f4becaa46ff247e0b9d90d4ab853b2a9";
  private static final String DEEZER_CLIENT_ID = "250322";
  private static final int REQUEST_CODE = 1334;

  private SkaldMusicService skaldMusicService;
  private Button playSpotifyButton;
  private Button playDeezerButton;
  private Button resumeButton;
  private Button pauseSpotifyButton;
  private Button stopButton;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    playSpotifyButton = (Button) findViewById(R.id.button_spotify_main_play);
    playDeezerButton = (Button) findViewById(R.id.button_deezer_main_play);
    resumeButton = (Button) findViewById(R.id.button_main_resume);
    pauseSpotifyButton = (Button) findViewById(R.id.button_main_pause);
    stopButton = (Button) findViewById(R.id.button_main_stop);

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
        Log.d(TAG, "Stop event");
      }

      @Override
      public void onError(PlaybackError playbackError) {
        Log.e(TAG, "Playback error occurred");
      }
    });
    skaldMusicService.addOnPreparedListener(new OnPreparedListener() {
      @Override
      public void onPrepared(final SkaldMusicService skaldMusicService) {
        playSpotifyButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            skaldMusicService.setSource(new SpotifyTrack(Uri.parse(
                "skald://spotify/track/spotify:track:4xkOaSrkexMciUUogZKVTS"),
                "Eminem", "Collapse"));
            skaldMusicService.play();
          }
        });
        playDeezerButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            skaldMusicService.setSource(
                new DeezerTrack(Uri.parse("skald://deezer/track/389296451"), "Taco", "Tlen"));
            skaldMusicService.play();
          }
        });
        resumeButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            skaldMusicService.resume();
          }
        });
        pauseSpotifyButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            skaldMusicService.pause();
          }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            skaldMusicService.stop();
          }
        });
      }
    });

    skaldMusicService.prepare();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        Log.d(TAG, "Authentication completed");
        skaldMusicService.prepare();
      } else {
        Log.e(TAG, "Authentication went wrong");
      }
    }
  }

  @Override
  protected void onDestroy() {
    skaldMusicService.release();
    super.onDestroy();
  }

  private void startAuthActivity(AuthError authError) {
    if (authError.hasResolution()) {
      Intent intent = authError.getResolution();
      startActivityForResult(intent, REQUEST_CODE);
    }
  }
}
