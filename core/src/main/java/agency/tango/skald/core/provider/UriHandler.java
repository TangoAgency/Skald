package agency.tango.skald.core.provider;

import android.net.Uri;

import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;

public class UriHandler {

  public static boolean isUriValid(SkaldPlayableEntity skaldPlayableEntity, String authorityName) {
    Uri uri = skaldPlayableEntity.getUri();
    boolean isSchemeValid = uri.getScheme().equals(SkaldPlayableEntity.SKALD_SCHEME);

    boolean isAuthorityValid = uri.getAuthority().equals(authorityName);

    String path = !uri.getPathSegments().isEmpty() ? uri.getPathSegments().get(0) : "";
    boolean isPathValid =
        (skaldPlayableEntity instanceof SkaldTrack && path.equals(SkaldTrack.PATH)) ||
            (skaldPlayableEntity instanceof SkaldPlaylist && path.equals(SkaldPlaylist.PATH));

    return isSchemeValid && isAuthorityValid && isPathValid;
  }
}
