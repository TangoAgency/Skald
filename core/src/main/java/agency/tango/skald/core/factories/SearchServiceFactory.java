package agency.tango.skald.core.factories;

import android.support.annotation.NonNull;
import agency.tango.skald.core.SearchService;
import agency.tango.skald.core.exceptions.AuthException;

public abstract class SearchServiceFactory {
  @NonNull
  public abstract SearchService getSearchService() throws AuthException;
}
