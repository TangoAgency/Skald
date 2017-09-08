package agency.tango.skald.core.models;

import android.net.Uri;

public class SkaldPlaylist {
  private final Uri uri;
  private final String name;

  public SkaldPlaylist(Uri uri, String name) {
    this.uri = uri;
    this.name = name;
  }

  public Uri getUri() {
    return uri;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "SkaldPlaylist{" +
        "uri=" + uri +
        ", name='" + name + '\'' +
        '}';
  }
}