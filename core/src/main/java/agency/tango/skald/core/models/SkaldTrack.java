package agency.tango.skald.core.models;

import android.net.Uri;

public class SkaldTrack extends SkaldPlayableEntity {
  private final String artistName;
  private final String title;

  public SkaldTrack(Uri uri, String artistName, String title, SkaldImage skaldImage) {
    super(uri, skaldImage);
    this.artistName = artistName;
    this.title = title;
  }

  public String getArtistName() {
    return artistName;
  }

  public String getTitle() {
    return title;
  }

  @Override
  public String toString() {
    return String.format("%s - %s", artistName, title);
  }
}
