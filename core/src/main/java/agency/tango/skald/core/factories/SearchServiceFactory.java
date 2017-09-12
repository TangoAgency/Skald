package agency.tango.skald.core.factories;

import agency.tango.skald.core.SearchService;
import agency.tango.skald.core.SkaldAuthData;

public abstract class SearchServiceFactory {
  public abstract SearchService getSearchService(SkaldAuthData skaldAuthData);
}
