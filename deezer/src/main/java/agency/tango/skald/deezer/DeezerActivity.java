package agency.tango.skald.deezer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.deezer.sdk.model.Permissions;
import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.connect.SessionStore;
import com.deezer.sdk.network.connect.event.DialogListener;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.player.TrackPlayer;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;
import com.deezer.sdk.player.networkcheck.WifiAndMobileNetworkStateChecker;

public class DeezerActivity extends Activity {
  private static final String APPLICATION_ID = "250322";
  private static final String TAG = DeezerActivity.class.getSimpleName();
  private DeezerConnect deezerConnect;
  private TrackPlayer trackPlayer;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_deezer);

    deezerConnect = new DeezerConnect(this, APPLICATION_ID);

    Button button = (Button) findViewById(R.id.button_login);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        authenticate(deezerConnect);
      }
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    trackPlayer.stop();
    trackPlayer.release();
  }

  private void authenticate(final DeezerConnect deezerConnect) {
    deezerConnect.authorize(this,
        new String[] {
            Permissions.BASIC_ACCESS,
            Permissions.MANAGE_LIBRARY,
            Permissions.LISTENING_HISTORY
        },
        new DialogListener() {
          @Override
          public void onComplete(Bundle bundle) {
            SessionStore sessionStore = new SessionStore();
            sessionStore.save(deezerConnect, getApplicationContext());
            Log.i(TAG, "Login completed");

            try {
              playMusic(deezerConnect);
            } catch (TooManyPlayersExceptions tooManyPlayersExceptions) {
              tooManyPlayersExceptions.printStackTrace();
            } catch (DeezerError deezerError) {
              deezerError.printStackTrace();
            }
          }

          @Override
          public void onCancel() {
            Log.i(TAG, "Login cancelled");
          }

          @Override
          public void onException(Exception e) {
            Log.e(TAG, "Login failed");
          }
        });
  }

  private void playMusic(DeezerConnect deezerConnect) throws DeezerError, TooManyPlayersExceptions {
    trackPlayer = new TrackPlayer(getApplication(), deezerConnect,
        new WifiAndMobileNetworkStateChecker());
    trackPlayer.playTrack(389296451);
  }
}