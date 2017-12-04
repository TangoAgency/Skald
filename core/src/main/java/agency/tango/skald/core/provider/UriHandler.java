package agency.tango.skald.core.provider;

import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;

public class UriHandler {

  public boolean isUriValid(SkaldPlayableEntity skaldPlayableEntity, String authorityName) {
    boolean isSchemeValid = skaldPlayableEntity.getScheme()
        .equals(SkaldPlayableEntity.SKALD_SCHEME);

    boolean isAuthorityValid = skaldPlayableEntity.getUri().getAuthority().equals(authorityName);

    String path = skaldPlayableEntity.getPath();
    boolean isPathValid =
        (skaldPlayableEntity instanceof SkaldTrack && path.equals(SkaldTrack.PATH)) ||
            (skaldPlayableEntity instanceof SkaldPlaylist && path.equals(SkaldPlaylist.PATH));

    return isSchemeValid && isAuthorityValid && isPathValid;
  }
}
