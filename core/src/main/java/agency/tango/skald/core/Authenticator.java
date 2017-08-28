package agency.tango.skald.core;

import android.app.Activity;
import android.content.Intent;

public interface Authenticator {
  void login(Activity activity);

  void logout();

  PlayerConfig retrievePlayerConfigFromLoginResult(int requestCode, int resultCode, Intent data);
}
