package agency.tango.skald.core.models;

import android.net.Uri;

public class SkaldTrack {
  private final Uri uri;
  private final String artistName;
  private final String title;

  public SkaldTrack(Uri uri, String artistName, String title) {
    this.uri = uri;
    this.artistName = artistName;
    this.title = title;
  }

  public Uri getUri() {
    return uri;
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
