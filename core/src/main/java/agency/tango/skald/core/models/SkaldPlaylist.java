package agency.tango.skald.core.models;

import android.net.Uri;

import java.util.List;

public class SkaldPlaylist extends SkaldPlayableEntity {
  private static final String PATH = "playlist";

  private String name;

  public SkaldPlaylist(Uri uri) {
    super(uri);
  }

  @Override
  public boolean verifyPath() {
    List<String> pathSegments = uri.getPathSegments();
    return !pathSegments.isEmpty() && pathSegments.get(0).equals(PATH);
  }

  public SkaldPlaylist(Uri uri, String imageUrl, String name) {
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