package agency.tango.skald.example;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import java.util.List;

import agency.tango.skald.R;
import agency.tango.skald.core.SkaldMusicService;
import agency.tango.skald.core.errors.AuthError;
import agency.tango.skald.core.listeners.OnAuthErrorListener;
import agency.tango.skald.core.listeners.OnErrorListener;
import agency.tango.skald.core.listeners.OnPreparedListener;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.deezer.DeezerProvider;
import agency.tango.skald.deezer.models.DeezerPlaylist;
import agency.tango.skald.deezer.models.DeezerTrack;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE;

public class DeezerActivity extends Activity {
  private static final String DEEZER_CLIENT_ID = "250322";
  private static final String TAG = agency.tango.skald.deezer.DeezerActivity.class.getSimpleName();
  private SkaldMusicService skaldMusicService;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_deezer);

    DeezerProvider deezerProvider = new DeezerProvider(this, DEEZER_CLIENT_ID);
    skaldMusicService = new SkaldMusicService(this, deezerProvider);

    skaldMusicService.addOnErrorListener(new OnErrorListener() {
      @Override
      public void onError() {
        Log.e(TAG, "Error in Deezer");
      }
    });
    skaldMusicService.addOnAuthErrorListener(new OnAuthErrorListener() {
      @Override
      public void onAuthError(AuthError authError) {
        startAuthActivity(authError);
      }
    });
    skaldMusicService.addOnPreparedListener(new OnPreparedListener() {
      @Override
      public void onPrepared(final SkaldMusicService skaldMusicService) {
        findViewById(R.id.button_play_track).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            skaldMusicService.setSource(
                new DeezerTrack(Uri.parse("skald://deezer/track/389296451"), "Taco", "Tlen", null));
            skaldMusicService.play();
          }
        });
        findViewById(R.id.button_play_playlist).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            skaldMusicService.setSource(
                new DeezerPlaylist(Uri.parse("skald://deezer/playlist/3188520162"), "Playlist"));
            skaldMusicService.play();
          }
        });
        findViewById(R.id.button_deezer_resume).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            skaldMusicService.resume();
          }
        });
        findViewById(R.id.button_deezer_pause).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            skaldMusicService.pause();
          }
        });
        findViewById((R.id.button_deezer_stop)).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            skaldMusicService.stop();
          }
        });

        skaldMusicService.searchTracks("hip-hop")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new DisposableSingleObserver<List<SkaldTrack>>() {
              @Override
              public void onSuccess(List<SkaldTrack> skaldTracks) {
                for(SkaldTrack skaldTrack : skaldTracks) {
                  Log.d(TAG, skaldTrack.toString());
                }
              }

              @Override
              public void onError(Throwable error) {
                Log.e(TAG, "Error occurred in observer during searching for tracks", error);
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

  private void startAuthActivity(AuthError authError) {
    if (authError.hasResolution()) {
      Intent intent = authError.getResolution();
      startActivityForResult(intent, REQUEST_CODE);
    }
  }

  @Override
  protected void onDestroy() {
    skaldMusicService.release();
    super.onDestroy();
  }
}
