package agency.tango.skald.core.models;

import android.support.annotation.NonNull;

public class TrackMetadata {
  @NonNull
  private String artistName;

  @NonNull
  private String title;

  private String imageUrl;

  public TrackMetadata(@NonNull String artistsName, @NonNull String title, String imageUrl) {
    this.artistName = artistsName;
    this.title = title;
    this.imageUrl = imageUrl;
  }

  @NonNull
  public String getArtistName() {
    return artistName;
  }

  @NonNull
  public String getTitle() {
    return title;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  @Override
  public String toString() {
    return "artist name='" + artistName + '\'' +
        ", title='" + title + '\'';
  }
}
