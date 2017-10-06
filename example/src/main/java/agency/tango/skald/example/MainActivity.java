package agency.tango.skald.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import agency.tango.skald.R;
import agency.tango.skald.core.AuthException;
import agency.tango.skald.core.Provider;
import agency.tango.skald.core.SkaldAuthService;
import agency.tango.skald.core.SkaldMusicService;
import agency.tango.skald.core.errors.AuthError;
import agency.tango.skald.core.errors.PlaybackError;
import agency.tango.skald.core.listeners.OnAuthErrorListener;
import agency.tango.skald.core.listeners.OnErrorListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.core.models.TrackMetadata;
import agency.tango.skald.deezer.DeezerAuthError;
import agency.tango.skald.spotify.SpotifyAuthError;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends Activity {
  private static final String TAG = MainActivity.class.getSimpleName();
  private static final int AUTH_SPOTIFY_REQUEST_CODE = 1334;
  private static final int AUTH_DEEZER_REQUEST_CODE = 1656;

  private SkaldMusicService skaldMusicService;
  private SkaldAuthService skaldAuthService;
  private ImageButton resumePauseButton;
  private ImageButton stopButton;
  private ImageView trackImage;
  private TextView artistName;
  private TextView title;
  private ListView listView;
  private Button spotifyButton;
  private Button deezerButton;
  private Button searchForTracksButton;
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
    spotifyButton = (Button) findViewById(R.id.button_login_spotify);
    deezerButton = (Button) findViewById(R.id.button_login_deezer);
    searchForTracksButton = (Button) findViewById(R.id.button_search);

    tracksAdapter = new TracksAdapter(getApplicationContext(), R.layout.row_layout);
    listView.setAdapter(tracksAdapter);

    skaldAuthService = new SkaldAuthService(getApplicationContext(), new OnAuthErrorListener() {
      @Override
      public void onAuthError(AuthError authError) {
        startAuthActivity(authError);
      }
    });

    skaldMusicService = new SkaldMusicService(getApplicationContext());

    addOnErrorListener();
    addOnPlaybackListener();

    setSpotifyButtonText();
    setDeezerButtonText();
    spotifyButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(!skaldAuthService.isLoggedIn(Provider.SPOTIFY_PROVIDER)) {
          skaldAuthService.login(Provider.SPOTIFY_PROVIDER);
          spotifyButton.setText(R.string.logout_from_spotify);
        }
        else {
          skaldAuthService.logout(Provider.SPOTIFY_PROVIDER);
          spotifyButton.setText(R.string.login_to_spotify);
        }
      }
    });
    deezerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(!skaldAuthService.isLoggedIn(Provider.DEEZER_PROVIDER)) {
          skaldAuthService.login(Provider.DEEZER_PROVIDER);
          deezerButton.setText(R.string.logout_from_deezer);
        }
        else {
          skaldAuthService.logout(Provider.DEEZER_PROVIDER);
          deezerButton.setText(R.string.login_to_deezer);
        }
      }
    });
    searchForTracksButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        searchTracks();
      }
    });

    resumePauseButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (isPlaying) {
          skaldMusicService.pause()
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(new PlaybackEventCompletableObserver());
        } else {
          skaldMusicService.resume()
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(new PlaybackEventCompletableObserver());
        }
      }
    });
    stopButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        skaldMusicService.stop()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new PlaybackEventCompletableObserver());
      }
    });

    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final SkaldTrack item = (SkaldTrack) parent.getItemAtPosition(position);
        skaldMusicService.play(item)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new DisposableSingleObserver<Object>() {
              @Override
              public void onSuccess(Object object) {
                Log.d(TAG, object.toString());
              }

              @Override
              public void onError(Throwable error) {
                if (error instanceof AuthException) {
                  AuthError authError = ((AuthException) error).getAuthError();
                  startAuthActivity(authError);
                }
              }
            });
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

    if (requestCode == AUTH_SPOTIFY_REQUEST_CODE || requestCode == AUTH_DEEZER_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        Log.d(TAG, "Authentication completed");
      } else {
        Log.e(TAG, "Authentication went wrong");
      }
    }
  }

  private void setSpotifyButtonText() {
    if(skaldAuthService.isLoggedIn(Provider.SPOTIFY_PROVIDER)) {
      spotifyButton.setText(R.string.logout_from_spotify);
    }
    else {
      spotifyButton.setText(R.string.login_to_spotify);
    }
  }

  private void setDeezerButtonText() {
    if(skaldAuthService.isLoggedIn(Provider.DEEZER_PROVIDER)) {
      deezerButton.setText(R.string.logout_from_deezer);
    }
    else {
      deezerButton.setText(R.string.login_to_deezer);
    }
  }

  private void addOnErrorListener() {
    skaldMusicService.addOnErrorListener(new OnErrorListener() {
      @Override
      public void onError() {
        Log.e(TAG, "Error in SkaldMusicService occurred");
      }
    });
  }

  private void addOnPlaybackListener() {
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
  }

  private void searchTracks() {
    skaldMusicService.searchTracks("hip-hop")
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new DisposableSingleObserver<List<SkaldTrack>>() {
          @Override
          public void onSuccess(List<SkaldTrack> skaldTracks) {
            tracksAdapter.clear();
            tracksAdapter.addAll(skaldTracks);
          }

          @Override
          public void onError(Throwable error) {
            if (error instanceof AuthException) {
              AuthError authError = ((AuthException) error).getAuthError();
              startAuthActivity(authError);
            } else {
              Log.e(TAG, "Error occurred in observer during searching for tracks", error);
            }
          }
        });
  }

  private void startAuthActivity(AuthError authError) {
    if (authError.hasResolution()) {
      if (authError instanceof SpotifyAuthError) {
        startActivityForResult(authError.getResolution(), AUTH_SPOTIFY_REQUEST_CODE);
      } else if (authError instanceof DeezerAuthError) {
        startActivityForResult(authError.getResolution(), AUTH_DEEZER_REQUEST_CODE);
      }
    }
  }

  private void notifyViews(TrackMetadata trackMetadata) {
    Picasso
        .with(this)
        .load(trackMetadata.getImageUrl())
        .into(trackImage);
    artistName.setText(trackMetadata.getArtistsName());
    title.setText(trackMetadata.getTitle());
  }

  private void notifyResumePauseButton() {
    if (isPlaying) {
      resumePauseButton.setImageResource(R.drawable.ic_action_pause);
    } else {
      resumePauseButton.setImageResource(R.drawable.ic_action_play);
    }
  }

  private class PlaybackEventCompletableObserver extends DisposableCompletableObserver {
    @Override
    public void onComplete() {

    }

    @Override
    public void onError(@NonNull Throwable e) {

    }
  }
}
