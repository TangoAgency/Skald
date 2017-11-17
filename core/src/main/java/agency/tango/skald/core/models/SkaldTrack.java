package agency.tango.skald.core.models;

import android.net.Uri;

public class SkaldTrack extends SkaldPlayableEntity {
  private String artistName;
  private String title;

  public SkaldTrack(Uri uri) {
    super(uri);
  }

  public SkaldTrack(Uri uri, String artistName, String title, String imageUrl) {
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
