package agency.tango.skald.core.models;

import android.net.Uri;

public class SkaldPlaylist extends SkaldPlayableEntity {
  private String name;

  public SkaldPlaylist(Uri uri) {
    super(uri);
  }

  public SkaldPlaylist(Uri uri, String name, String imageUrl) {
    super(uri, imageUrl);
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}