package agency.tango.skald.spotify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import static agency.tango.skald.core.SkaldMusicService.EXTRA_AUTH_DATA;
import static agency.tango.skald.core.SkaldMusicService.INTENT_ACTION;
import static agency.tango.skald.spotify.SpotifyProvider.EXTRA_CLIENT_ID;
import static agency.tango.skald.spotify.SpotifyProvider.EXTRA_REDIRECT_URI;
import static com.spotify.sdk.android.authentication.AuthenticationResponse.Type.TOKEN;

public class SpotifyAuthActivity extends Activity {
  private static final int REQUEST_CODE = 12334;
  private static final String TAG = SpotifyAuthActivity.class.getSimpleName();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    String clientId = getIntent().getStringExtra(EXTRA_CLIENT_ID);
    String redirectUri = getIntent().getStringExtra(EXTRA_REDIRECT_URI);

    AuthenticationRequest request = new AuthenticationRequest.Builder(clientId, TOKEN, redirectUri)
        .setScopes(new String[] { "user-read-private", "streaming" })
        .build();

    AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == REQUEST_CODE) {
      AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
      switch (response.getType()) {
        case TOKEN:
          notifyAuthData(response.getAccessToken(), response.getExpiresIn());
          break;
        case CODE:
          notifyAuthData(response.getCode());
        case ERROR:
          notifyError(response.getError());
      }
    }

  }

  private void notifyError(String error) {

  }

  private void notifyAuthData(String accessToken, int expiresIn) {
    Log.d(TAG, accessToken);

    SpotifyAuthData spotifyAuthorizationData =
        new SpotifyAuthData(accessToken, expiresIn);
    Intent intent = new Intent(INTENT_ACTION);
    intent.putExtra(EXTRA_AUTH_DATA, spotifyAuthorizationData);
    LocalBroadcastManager
        .getInstance(this)
        .sendBroadcast(intent);
  }

  private void notifyAuthData(String code) {
    //TODO getTokens call and send broadcast
  }
}
