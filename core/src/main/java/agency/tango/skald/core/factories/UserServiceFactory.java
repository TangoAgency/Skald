package agency.tango.skald.core.factories;

import android.support.annotation.NonNull;
import agency.tango.skald.core.UserService;
import agency.tango.skald.core.exceptions.AuthException;

public abstract class UserServiceFactory {
  @NonNull
  public abstract UserService getUserService() throws AuthException;
}
