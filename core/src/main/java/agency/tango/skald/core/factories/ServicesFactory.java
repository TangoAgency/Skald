package agency.tango.skald.core.factories;

import agency.tango.skald.core.SearchService;
import agency.tango.skald.core.UserService;

public abstract class ServicesFactory {
  public abstract SearchService getSearchService();

  public abstract UserService getUserService();
}
