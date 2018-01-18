package agency.tango.skald.core.models;

import android.net.Uri;
import android.support.annotation.NonNull;

public abstract class SkaldPlayableEntity {
  public static final String SKALD_SCHEME = "skald";

  @NonNull
  protected final Uri uri;

  private final String imageUrl;

  public SkaldPlayableEntity(Uri uri) {
    this(uri, "");
  }

  public SkaldPlayableEntity(@NonNull Uri uri, String imageUrl) {
    this.uri = uri;
    this.imageUrl = imageUrl;
  }

  @NonNull
  public Uri getUri() {
    return uri;
  }

  public String getImageUrl() {
    return imageUrl;
  }
}
