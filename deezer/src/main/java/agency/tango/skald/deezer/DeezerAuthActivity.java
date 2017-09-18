package agency.tango.skald.deezer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.deezer.sdk.model.Permissions;
import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.connect.event.DialogListener;

import static agency.tango.skald.core.SkaldMusicService.EXTRA_AUTH_DATA;
import static agency.tango.skald.core.SkaldMusicService.EXTRA_PROVIDER_NAME;
import static agency.tango.skald.core.SkaldMusicService.INTENT_ACTION;
import static agency.tango.skald.deezer.DeezerProvider.DEEZER_PROVIDER;
import static agency.tango.skald.deezer.DeezerProvider.EXTRA_CLIENT_ID;

public class DeezerAuthActivity extends Activity {
  private static final String TAG = DeezerAuthActivity.class.getSimpleName();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    String clientId = getIntent().getStringExtra(EXTRA_CLIENT_ID);

    final DeezerConnect deezerConnect = new DeezerConnect(getApplicationContext(), clientId);

    deezerConnect.authorize(this,
        new String[] {
            Permissions.BASIC_ACCESS,
            Permissions.MANAGE_LIBRARY,
            Permissions.LISTENING_HISTORY
        },
        new DialogListener() {
          @Override
          public void onComplete(Bundle bundle) {
            DeezerAuthData deezerAuthData = new DeezerAuthData(deezerConnect);

            Intent intent = new Intent(INTENT_ACTION);
            intent.putExtra(EXTRA_PROVIDER_NAME, DEEZER_PROVIDER);
            intent.putExtra(EXTRA_AUTH_DATA, deezerAuthData);
            LocalBroadcastManager
                .getInstance(DeezerAuthActivity.this)
                .sendBroadcast(intent);

            setResult(RESULT_OK);

            finish();
          }

          @Override
          public void onCancel() {
            Log.e(TAG, "Login canceled");
            setResult(RESULT_CANCELED);
          }

          @Override
          public void onException(Exception exception) {
            Log.e(TAG, "Login failed", exception);
            setResult(RESULT_CANCELED);
          }
        });
  }
}
