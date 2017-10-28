package agency.tango.skald.core.factories;

import agency.tango.skald.core.exceptions.AuthException;
import agency.tango.skald.core.SearchService;

public abstract class SearchServiceFactory {
  public abstract SearchService getSearchService() throws AuthException;
}
