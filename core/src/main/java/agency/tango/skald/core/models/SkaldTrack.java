package agency.tango.skald.core.models;

import android.net.Uri;

public class SkaldTrack extends SkaldPlayableEntity {
  private final Uri uri;
  private final String artistName;
  private final String title;
  private final String imageUrl;

  public SkaldTrack(Uri uri, String artistName, String title, String imageUrl) {
    this.uri = uri;
    this.artistName = artistName;
    this.title = title;
    this.imageUrl = imageUrl;
  }

  @Override
  public Uri getUri() {
    return uri;
  }

  public String getArtistName() {
    return artistName;
  }

  public String getTitle() {
    return title;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  @Override
  public String toString() {
    return String.format("%s - %s", artistName, title);
  }
}
