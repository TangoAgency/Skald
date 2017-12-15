package agency.tango.skald.core.models;

import android.net.Uri;

public abstract class SkaldPlayableEntity {
  public static final String SKALD_SCHEME = "skald";

  protected final Uri uri;
  private final String imageUrl;

  public SkaldPlayableEntity(Uri uri) {
    this(uri, "");
  }

  public SkaldPlayableEntity(Uri uri, String imageUrl) {
    this.uri = uri;
    this.imageUrl = imageUrl;
  }

  public Uri getUri() {
    return uri;
  }

  public String getImageUrl() {
    return imageUrl;
  }
}
