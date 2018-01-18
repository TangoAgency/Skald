package agency.tango.skald.spotify.authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import agency.tango.skald.spotify.api.models.Tokens;
import agency.tango.skald.spotify.services.TokenService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static agency.tango.skald.core.SkaldMusicService.EXTRA_AUTH_DATA;
import static agency.tango.skald.core.SkaldMusicService.EXTRA_PROVIDER_NAME;
import static agency.tango.skald.core.SkaldMusicService.INTENT_ACTION;
import static agency.tango.skald.core.SkaldMusicService.INTENT_ACTION_ERROR;
import static agency.tango.skald.spotify.provider.SpotifyProvider.EXTRA_CLIENT_ID;
import static agency.tango.skald.spotify.provider.SpotifyProvider.EXTRA_CLIENT_SECRET;
import static agency.tango.skald.spotify.provider.SpotifyProvider.EXTRA_REDIRECT_URI;
import static agency.tango.skald.spotify.provider.SpotifyProvider.NAME;
import static com.spotify.sdk.android.authentication.AuthenticationResponse.Type.CODE;

public class SpotifyAuthActivity extends Activity {
  private static final int REQUEST_CODE = 12334;
  private static final String TAG = SpotifyAuthActivity.class.getSimpleName();
  private String clientId;
  private String redirectUri;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    clientId = getIntent().getStringExtra(EXTRA_CLIENT_ID);
    redirectUri = getIntent().getStringExtra(EXTRA_REDIRECT_URI);

    AuthenticationRequest request = new AuthenticationRequest.Builder(clientId, CODE, redirectUri)
        .setScopes(new String[] {"user-read-private", "streaming"})
        .build();

    AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == REQUEST_CODE) {
      AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
      switch (response.getType()) {
        case CODE:
          notifyAuthData(response.getCode());
          break;
        case ERROR:
          notifyError(response.getError());
          break;
        default:
          setResult(RESULT_CANCELED);
          this.finish();
      }
    }
  }

  private void notifyAuthData(String code) {
    String clientSecret = getIntent().getStringExtra(EXTRA_CLIENT_SECRET);
    new TokenService()
        .getTokens(clientId, clientSecret, code, redirectUri)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new DisposableSingleObserver<Tokens>() {
          @Override
          public void onSuccess(Tokens tokens) {
            SpotifyAuthData spotifyAuthData = new SpotifyAuthData(tokens.getAccessToken(),
                tokens.getRefreshToken(), tokens.getExpiresIn());

            Intent intent = new Intent(INTENT_ACTION);
            intent.putExtra(EXTRA_PROVIDER_NAME, NAME.getName());
            intent.putExtra(EXTRA_AUTH_DATA, spotifyAuthData);
            LocalBroadcastManager
                .getInstance(SpotifyAuthActivity.this)
                .sendBroadcast(intent);

            setResult(RESULT_OK);
            SpotifyAuthActivity.this.finish();
          }

          @Override
          public void onError(Throwable error) {
            notifyError(String.format("Cannot get auth tokens %s", error.toString()));
          }
        });
  }

  private void notifyError(String error) {
    Log.e(TAG, String.format("Spotify authentication request error %s", error));

    Intent intent = new Intent(INTENT_ACTION_ERROR);
    intent.putExtra(EXTRA_PROVIDER_NAME, NAME.getName());
    LocalBroadcastManager
        .getInstance(this)
        .sendBroadcast(intent);

    setResult(RESULT_CANCELED);
    this.finish();
  }
}
