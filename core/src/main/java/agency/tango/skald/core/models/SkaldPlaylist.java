package agency.tango.skald.core.models;

import android.net.Uri;

public class SkaldPlaylist extends SkaldPlayableEntity {
  public static final String PATH = "playlist";

  private final String name;

  public SkaldPlaylist(Uri uri) {
    super(uri);
    name = "";
  }

  public SkaldPlaylist(Uri uri, String imageUrl, String name) {
    super(uri, imageUrl);
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