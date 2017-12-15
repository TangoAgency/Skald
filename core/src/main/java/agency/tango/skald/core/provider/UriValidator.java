package agency.tango.skald.core.provider;

import android.net.Uri;
import java.util.HashMap;
import java.util.Map;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;

public class UriValidator {
  private static final Map<Class, String> entitiesMap;
  private static final String EMPTY = "";

  static {
    entitiesMap = new HashMap<>();
    entitiesMap.put(SkaldTrack.class, SkaldTrack.URI_PATH_FIRST_SEGMENT);
    entitiesMap.put(SkaldPlaylist.class, SkaldPlaylist.URI_PATH_FIRST_SEGMENT);
  }

  public static boolean validate(SkaldPlayableEntity skaldPlayableEntity, String authorityName) {
    Uri uri = skaldPlayableEntity.getUri();

    return isSchemeValid(uri) && isAuthorityValid(authorityName, uri) &&
        isPathValid(skaldPlayableEntity, uri);
  }

  private static boolean isSchemeValid(Uri uri) {
    return SkaldPlayableEntity.SKALD_SCHEME.equals(uri.getScheme());
  }

  private static boolean isAuthorityValid(String authorityName, Uri uri) {
    return authorityName.equals(uri.getAuthority());
  }

  private static boolean isPathValid(SkaldPlayableEntity skaldPlayableEntity, Uri uri) {
    String pathFromMap = entitiesMap.get(skaldPlayableEntity.getClass());
    if (pathFromMap == null) {
      pathFromMap = entitiesMap.get(skaldPlayableEntity.getClass().getSuperclass());
    }

    return getPathFromUri(uri).equals(pathFromMap);
  }

  private static String getPathFromUri(Uri uri) {
    return !uri.getPathSegments().isEmpty() ? uri.getPathSegments().get(0) : EMPTY;
  }
}
