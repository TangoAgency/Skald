package agency.tango.skald.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
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
import agency.tango.skald.core.provider.ProviderName;
import agency.tango.skald.deezer.errors.DeezerAuthError;
import agency.tango.skald.deezer.provider.DeezerProvider;
import agency.tango.skald.spotify.errors.SpotifyAuthError;
import agency.tango.skald.spotify.provider.SpotifyProvider;
import io.reactivex.Completable;
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
  private TextView userName;
  private ImageView userAvatar;
  private ListView listView;
  private ProgressBar loadingTrackProgressBar;
  private ProgressBar loadingListProgressBar;

  private SkaldEntityAdapter adapter;
  private boolean isPlaying = false;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    resumePauseButton = findViewById(R.id.imageButton_play);
    stopButton = findViewById(R.id.imageButton_stop);
    trackImage = findViewById(R.id.image_cover);
    artistName = findViewById(R.id.text_artist);
    title = findViewById(R.id.text_title);
    spotifyButton = findViewById(R.id.button_login_spotify);
    deezerButton = findViewById(R.id.button_login_deezer);
    tracksButton = findViewById(R.id.button_tracks);
    playlistButton = findViewById(R.id.button_playlists);
    userName = findViewById(R.id.text_user_name);
    userAvatar = findViewById(R.id.image_user);
    adapter = new SkaldEntityAdapter(this);
    listView = findViewById(R.id.list_view_tracks);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener((parent, listView, position, id) -> {
      loadingTrackProgressBar.setVisibility(View.VISIBLE);
      setTrackInfoVisibility(false);
      final SkaldPlayableEntity item = (SkaldPlayableEntity) parent.getItemAtPosition(position);
      play(item);
    });
    loadingTrackProgressBar = findViewById(R.id.loading_track_progress_bar);
    loadingListProgressBar = findViewById(R.id.loading_list_progress_bar);

    skaldAuthService = new SkaldAuthService(getApplicationContext(), this::startAuthActivity);
    skaldMusicService = new SkaldMusicService(getApplicationContext());

    skaldMusicService.addOnErrorListener(
        exception -> Log.e(TAG, "Error in SkaldMusicService occurred", exception));

    addOnPlaybackListener();

    skaldMusicService.addOnLoadingListener(() -> Log.d(TAG, "Loading track..."));

    spotifyButton.setOnClickListener(
        v -> authenticateProvider(SpotifyProvider.NAME, spotifyButton, R.string.login_to_spotify));

    deezerButton.setOnClickListener(
        v -> authenticateProvider(DeezerProvider.NAME, deezerButton, R.string.login_to_deezer));


    tracksButton.setOnClickListener(v -> searchTracks());
    playlistButton.setOnClickListener(v -> searchPlaylists());

    resumePauseButton.setOnClickListener(v -> {
      if (isPlaying) {
        runOnSchedulers(skaldMusicService.pause())
            .subscribe(new PlaybackEventCompletableObserver());
      } else {
        runOnSchedulers(skaldMusicService.resume())
            .subscribe(new PlaybackEventCompletableObserver());
      }
    });

    stopButton.setOnClickListener(v -> runOnSchedulers(skaldMusicService.stop())
        .subscribe(new PlaybackEventCompletableObserver()));

    searchTracks();
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

    if (requestCode == AUTH_SPOTIFY_REQUEST_CODE) {
      handleAuthenticationResult(resultCode, spotifyButton, R.string.logout_from_spotify);
    } else if (requestCode == AUTH_DEEZER_REQUEST_CODE) {
      handleAuthenticationResult(resultCode, deezerButton, R.string.logout_from_deezer);
    }
  }

  private Completable runOnSchedulers(Completable completable) {
    return completable.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  private void authenticateProvider(final ProviderName providerName, Button button,
      @StringRes int loginText) {
    if (!skaldAuthService.isLoggedIn(providerName)) {
      skaldAuthService.login(providerName);
    } else {
      skaldAuthService.logout(providerName);
      button.setText(loginText);
      updateViewsAfterLoggingOut();
    }
  }

  private void setTrackInfoVisibility(boolean isVisible) {
    int visibility = isVisible ? View.VISIBLE : View.INVISIBLE;
    artistName.setVisibility(visibility);
    title.setVisibility(visibility);
    trackImage.setVisibility(visibility);
  }

  private void play(SkaldPlayableEntity skaldPlayableEntity) {
    runOnSchedulers(skaldMusicService.play(skaldPlayableEntity))
        .subscribe(new DisposableCompletableObserver() {
          @Override
          public void onComplete() {
            Log.d(TAG, "Play completed");
          }

          @Override
          public void onError(Throwable error) {
            Log.e(TAG, "Error occurred when try to play entity", error);
            if (error instanceof AuthException) {
              AuthError authError = ((AuthException) error).getAuthError();
              startAuthActivity(authError);
            }
          }
        });
  }

  private void searchTracks() {
    adapter.clear();
    loadingListProgressBar.setVisibility(View.VISIBLE);
    skaldMusicService.searchTracks("rock")
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new DisposableSingleObserver<List<SkaldTrack>>() {
          @Override
          public void onSuccess(List<SkaldTrack> skaldTracks) {
            loadingListProgressBar.setVisibility(View.GONE);
            adapter.addAll(skaldTracks);
          }

          @Override
          public void onError(Throwable error) {
            loadingListProgressBar.setVisibility(View.GONE);
            Log.e(TAG, "Error during searching tracks", error);
          }
        });
  }

  private void searchPlaylists() {
    adapter.clear();
    loadingListProgressBar.setVisibility(View.VISIBLE);
    skaldMusicService.searchPlayLists("hip-hop")
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new DisposableSingleObserver<List<SkaldPlaylist>>() {
          @Override
          public void onSuccess(List<SkaldPlaylist> skaldPlaylists) {
            loadingListProgressBar.setVisibility(View.GONE);
            adapter.addAll(skaldPlaylists);
          }

          @Override
          public void onError(Throwable error) {
            loadingListProgressBar.setVisibility(View.GONE);
            Log.e(TAG, "Error during searching playlists", error);
          }
        });
  }

  private void updateViewsAfterLoggingOut() {
    if (!isUserLoggedIn()) {
      clearUserViews();
      userName.setText(R.string.hello);
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
              String message = String.format(getString(R.string.hello_user_name),
                  skaldUsers.get(0).getNickName());
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

  private void clearUserViews() {
    updateUserViews(EMPTY, null);
  }

  private void updateUserViews(String message, String imageUrl) {
    userName.setText(message);
    drawAnImage(imageUrl, userAvatar, R.drawable.ic_person_24dp_black);
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

  private void handleAuthenticationResult(int resultCode, Button serviceButton,
      @StringRes int buttonText) {
    if (resultCode == RESULT_OK) {
      serviceButton.setText(buttonText);
    } else {
      Log.e(TAG, "Authentication went wrong");
      Toast.makeText(this, R.string.authentication_error, Toast.LENGTH_LONG)
          .show();
    }
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
        Log.e(TAG, "Playback error occurred", playbackError.getException());
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
    loadingTrackProgressBar.setVisibility(View.GONE);
    setTrackInfoVisibility(true);
    drawAnImage(trackMetadata.getImageUrl(), trackImage, R.drawable.ic_track_image_24dp_black);
    artistName.setText(trackMetadata.getArtistsName());
    title.setText(trackMetadata.getTitle());
  }

  private void drawAnImage(String imageUrl, ImageView imageView, @DrawableRes int drawable) {
    Picasso
        .with(this)
        .load(imageUrl)
        .placeholder(drawable)
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
      Log.d(TAG, "Operation completed");
    }

    @Override
    public void onError(@NonNull Throwable e) {
      Log.e(TAG, "Error during playback operation", e);
    }
  }
}
