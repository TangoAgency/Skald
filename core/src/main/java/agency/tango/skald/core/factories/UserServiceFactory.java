package agency.tango.skald.core.factories;

import agency.tango.skald.core.UserService;
import agency.tango.skald.core.exceptions.AuthException;

public abstract class UserServiceFactory {
  public abstract UserService getUserService() throws AuthException;
}
