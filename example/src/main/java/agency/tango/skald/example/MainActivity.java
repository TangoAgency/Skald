package agency.tango.skald.example;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import agency.tango.skald.R;
import agency.tango.skald.core.SkaldAuthService;
import agency.tango.skald.core.SkaldMusicService;
import agency.tango.skald.core.errors.AuthError;
import agency.tango.skald.core.errors.PlaybackError;
import agency.tango.skald.core.exceptions.AuthException;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.core.models.SkaldUser;
import agency.tango.skald.core.models.TrackMetadata;
import agency.tango.skald.deezer.errors.DeezerAuthError;
import agency.tango.skald.deezer.provider.DeezerProvider;
import agency.tango.skald.spotify.errors.SpotifyAuthError;
import agency.tango.skald.spotify.provider.SpotifyProvider;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends Activity {
  private static final String TAG = MainActivity.class.getSimpleName();
  private static final int AUTH_SPOTIFY_REQUEST_CODE = 1334;
  private static final int AUTH_DEEZER_REQUEST_CODE = 1656;
  private static final String EMPTY = "";

  private SkaldMusicService skaldMusicService;
  private SkaldAuthService skaldAuthService;
  private ImageButton resumePauseButton;
  private ImageButton stopButton;
  private ImageView trackImage;
  private TextView artistName;
  private TextView title;
  private Button spotifyButton;
  private Button deezerButton;
  private Button tracksButton;
  private Button playlistButton;
  private TextView loadingTextView;
  private TextView userName;
  private ImageView userAvatar;

  private boolean isPlaying = false;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    resumePauseButton = (ImageButton) findViewById(R.id.imageButton_play);
    stopButton = (ImageButton) findViewById(R.id.imageButton_stop);
    trackImage = (ImageView) findViewById(R.id.image_cover);
    artistName = (TextView) findViewById(R.id.text_artist);
    title = (TextView) findViewById(R.id.text_title);
    spotifyButton = (Button) findViewById(R.id.button_login_spotify);
    deezerButton = (Button) findViewById(R.id.button_login_deezer);
    tracksButton = (Button) findViewById(R.id.button_tracks);
    playlistButton = (Button) findViewById(R.id.button_playlists);
    loadingTextView = (TextView) findViewById(R.id.text_loading);
    userName = findViewById(R.id.text_user_name);
    userAvatar = findViewById(R.id.image_user);

    skaldAuthService = new SkaldAuthService(getApplicationContext(), this::startAuthActivity);

    skaldMusicService = new SkaldMusicService(getApplicationContext());

    addOnErrorListener();
    addOnPlaybackListener();
    skaldMusicService.addOnLoadingListener(() -> {
      Log.d(TAG, "Loading track...");
      loadingTextView.setText(R.string.loading);
    });

    spotifyButton.setOnClickListener(v -> {
      if (!skaldAuthService.isLoggedIn(SpotifyProvider.NAME)) {
        skaldAuthService.login(SpotifyProvider.NAME);
        spotifyButton.setText(R.string.logout_from_spotify);
      } else {
        skaldAuthService.logout(SpotifyProvider.NAME);
        spotifyButton.setText(R.string.login_to_spotify);
        updateViewsAfterLoggingOut();
      }
    });
    deezerButton.setOnClickListener(v -> {
      if (!skaldAuthService.isLoggedIn(DeezerProvider.NAME)) {
        skaldAuthService.login(DeezerProvider.NAME);
        deezerButton.setText(R.string.logout_from_deezer);
      } else {
        skaldAuthService.logout(DeezerProvider.NAME);
        deezerButton.setText(R.string.login_to_deezer);
        updateViewsAfterLoggingOut();
      }
    });

    tracksButton.setOnClickListener(v -> {
      FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
      TrackListFragment fragment = new TrackListFragment();
      fragmentTransaction.replace(R.id.fragment_container, fragment);
      fragmentTransaction.commit();
      getFragmentManager().executePendingTransactions();
      fragment.searchTracks();
    });

    playlistButton.setOnClickListener(v -> {
      FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
      PlaylistListFragment fragment = new PlaylistListFragment();
      fragmentTransaction.replace(R.id.fragment_container, fragment);
      fragmentTransaction.commit();
      getFragmentManager().executePendingTransactions();
      fragment.searchPlaylists();
    });

    resumePauseButton.setOnClickListener(v -> {
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
    });
    stopButton.setOnClickListener(v -> skaldMusicService.stop()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new PlaybackEventCompletableObserver()));
  }

  @Override
  protected void onResume() {
    super.onResume();

    setSpotifyButtonText();
    setDeezerButtonText();
    getUsersAndUpdateUserViews();
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

  public void play(SkaldPlayableEntity skaldPlayableEntity) {
    skaldMusicService.play(skaldPlayableEntity)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new DisposableCompletableObserver() {
          @Override
          public void onComplete() {
            Log.d(TAG, "Played complete");
          }

          @Override
          public void onError(Throwable error) {
            Log.e(TAG, "Error during playing sth", error);
            if (error instanceof AuthException) {
              AuthError authError = ((AuthException) error).getAuthError();
              startAuthActivity(authError);
            }
          }
        });
  }

  public void searchTracks(final TracksAdapter tracksAdapter) {
    skaldMusicService.searchTracks("rock")
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
            Log.e(TAG, "Error during searching tracks", error);
          }
        });
  }

  public void searchPlaylists(final PlaylistAdapter playlistAdapter) {
    skaldMusicService.searchPlayLists("hip-hop")
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new DisposableSingleObserver<List<SkaldPlaylist>>() {
          @Override
          public void onSuccess(List<SkaldPlaylist> skaldPlaylists) {
            playlistAdapter.clear();
            playlistAdapter.addAll(skaldPlaylists);
          }

          @Override
          public void onError(Throwable error) {
            Log.e(TAG, "Error during searching playlists", error);
          }
        });
  }

  private void updateViewsAfterLoggingOut() {
    if (!isUserLoggedIn()) {
      updateUserViews(EMPTY, null);
      userName.setText(R.string.hello_exc_mark);
    } else {
      getUsersAndUpdateUserViews();
    }
  }

  private void getUsersAndUpdateUserViews() {
    if (isUserLoggedIn()) {
      skaldMusicService.getCurrentUsers()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new DisposableSingleObserver<List<SkaldUser>>() {
            @Override
            public void onSuccess(List<SkaldUser> skaldUsers) {
              String message = String.format("Hello, %s!", skaldUsers.get(0).getNickName());
              updateUserViews(message, skaldUsers.get(0).getImageUrl());
            }

            @Override
            public void onError(Throwable e) {
              Log.e(TAG, "Error during getting player");
            }
          });
    }
  }

  private boolean isUserLoggedIn() {
    return skaldAuthService.isLoggedIn(SpotifyProvider.NAME) ||
        skaldAuthService.isLoggedIn(DeezerProvider.NAME);
  }

  private void updateUserViews(String message, String imageUrl) {
    userName.setText(message);
    drawAnImage(imageUrl, userAvatar);
  }

  private void setSpotifyButtonText() {
    if (skaldAuthService.isLoggedIn(SpotifyProvider.NAME)) {
      spotifyButton.setText(R.string.logout_from_spotify);
    } else {
      spotifyButton.setText(R.string.login_to_spotify);
    }
  }

  private void setDeezerButtonText() {
    if (skaldAuthService.isLoggedIn(DeezerProvider.NAME)) {
      deezerButton.setText(R.string.logout_from_deezer);
    } else {
      deezerButton.setText(R.string.login_to_deezer);
    }
  }

  private void addOnErrorListener() {
    skaldMusicService.addOnErrorListener(() -> Log.e(TAG, "Error in SkaldMusicService occurred"));
  }

  private void addOnPlaybackListener() {
    skaldMusicService.addOnPlaybackListener(new OnPlaybackListener() {
      @Override
      public void onPlayEvent(TrackMetadata trackMetadata) {
        Log.d(TAG, String.format("%s - %s", trackMetadata.getArtistsName(),
            trackMetadata.getTitle()));
        isPlaying = true;
        updateSongViews(trackMetadata);
      }

      @Override
      public void onPauseEvent() {
        Log.d(TAG, "Pause Event");
        isPlaying = false;
        updateResumePauseButton();
      }

      @Override
      public void onResumeEvent() {
        Log.d(TAG, "Resume Event");
        isPlaying = true;
        updateResumePauseButton();
      }

      @Override
      public void onStopEvent() {
        Log.d(TAG, "Stop event");
        isPlaying = false;
      }

      @Override
      public void onError(PlaybackError playbackError) {
        Log.e(TAG, String.format("Playback error occurred %s", playbackError.getMessage()));
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

  private void updateSongViews(TrackMetadata trackMetadata) {
    drawAnImage(trackMetadata.getImageUrl(), trackImage);
    artistName.setText(trackMetadata.getArtistsName());
    title.setText(trackMetadata.getTitle());
    loadingTextView.setText(EMPTY);
  }

  private void drawAnImage(String imageUrl, ImageView imageView) {
    Picasso
        .with(this)
        .load(imageUrl)
        .into(imageView);
  }

  private void updateResumePauseButton() {
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
