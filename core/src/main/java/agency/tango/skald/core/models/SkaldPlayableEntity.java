package agency.tango.skald.core.models;

import android.net.Uri;

public abstract class SkaldPlayableEntity {
  private final Uri uri;
  private String imageUrl;

  public SkaldPlayableEntity(Uri uri) {
    this.uri = uri;
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

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getServiceNameFromUri() {
    return uri.getAuthority();
  }
}
