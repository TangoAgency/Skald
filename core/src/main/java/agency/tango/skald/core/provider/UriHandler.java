package agency.tango.skald.core.provider;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;

public class UriHandler {
  private static final Map<Class, String> entitiesMap;

  static {
    entitiesMap = new HashMap<>();
    entitiesMap.put(SkaldTrack.class, SkaldTrack.PATH);
    entitiesMap.put(SkaldPlaylist.class, SkaldPlaylist.PATH);
  }

  public static boolean isUriValid(SkaldPlayableEntity skaldPlayableEntity, String authorityName) {
    Uri uri = skaldPlayableEntity.getUri();
    boolean isSchemeValid = uri.getScheme().equals(SkaldPlayableEntity.SKALD_SCHEME);

    boolean isAuthorityValid = uri.getAuthority().equals(authorityName);

    String pathFromMap = entitiesMap.get(skaldPlayableEntity.getClass());
    if(pathFromMap == null) {
      pathFromMap = entitiesMap.get(skaldPlayableEntity.getClass().getSuperclass());
    }

    String pathFromUri = !uri.getPathSegments().isEmpty() ? uri.getPathSegments().get(0) : "";
    boolean isPathValid = pathFromMap.equals(pathFromUri);

    return isSchemeValid && isAuthorityValid && isPathValid;
  }
}
