package agency.tango.skald.core.factories;

import agency.tango.skald.core.SearchService;
import agency.tango.skald.core.exceptions.AuthException;

public abstract class SearchServiceFactory {
  public abstract SearchService getSearchService() throws AuthException;
}
