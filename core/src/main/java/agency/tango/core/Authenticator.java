package agency.tango.core;

import android.app.Activity;

public interface Authenticator {
  void login(Activity parentActivity);

  void logout();
}
