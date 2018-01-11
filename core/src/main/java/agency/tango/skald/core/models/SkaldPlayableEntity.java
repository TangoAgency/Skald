package agency.tango.skald.core.models;

import android.net.Uri;

public abstract class SkaldPlayableEntity {
  private final Uri uri;
  private final SkaldImage skaldImage;

  public SkaldPlayableEntity(Uri uri, SkaldImage skaldImage) {
    this.uri = uri;
    this.skaldImage = skaldImage;
  }

  public Uri getUri() {
    return uri;
  }

  public SkaldImage getImage() {
    return skaldImage;
  }
}
