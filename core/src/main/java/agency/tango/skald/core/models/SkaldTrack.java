package agency.tango.skald.core.models;

import android.net.Uri;

import java.util.List;

public class SkaldTrack extends SkaldPlayableEntity {
  private static final String PATH = "track";

  private String artistName;
  private String title;

  public SkaldTrack(Uri uri) {
    super(uri);
  }

  @Override
  public boolean verifyPath() {
    List<String> pathSegments = uri.getPathSegments();
    return !pathSegments.isEmpty() && pathSegments.get(0).equals(PATH);
  }

  public SkaldTrack(Uri uri, String imageUrl, String artistName, String title) {
    super(uri, imageUrl);
    this.artistName = artistName;
    this.title = title;
  }

  public String getArtistName() {
    return artistName;
  }

  public String getTitle() {
    return title;
  }

  public void setArtistName(String artistName) {
    this.artistName = artistName;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String toString() {
    return String.format("%s - %s", artistName, title);
  }
}
