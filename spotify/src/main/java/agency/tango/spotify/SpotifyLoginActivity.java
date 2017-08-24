package agency.tango.spotify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class SpotifyLoginActivity extends Activity {
  private SpotifyAuthenticator authorizator;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  @Override
  protected void onStart() {
    super.onStart();
    authorizator.login(this);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    authorizator.handleLoginResponse(this, requestCode, resultCode, data);
  }
}
