package agency.tango.skald.core.models;

import android.net.Uri;

public class SkaldPlaylist extends SkaldPlayableEntity {
  private final String name;

  public SkaldPlaylist(Uri uri, String name, SkaldImage skaldImage) {
    super(uri, skaldImage);
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}