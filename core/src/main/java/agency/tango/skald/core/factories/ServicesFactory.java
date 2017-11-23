package agency.tango.skald.core.factories;

import agency.tango.skald.core.UserService;
import agency.tango.skald.core.exceptions.AuthException;
import agency.tango.skald.core.SearchService;

public abstract class ServicesFactory {
  public abstract SearchService getSearchService() throws AuthException;

  public abstract UserService getUserService() throws AuthException;
}
