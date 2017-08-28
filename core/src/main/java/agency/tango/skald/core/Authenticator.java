package agency.tango.skald.core;

import android.app.Activity;

public interface Authenticator {
  void login(Activity activity);

  void logout();
}
