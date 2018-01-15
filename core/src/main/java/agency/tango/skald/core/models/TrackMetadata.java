package agency.tango.skald.core.models;

import android.support.annotation.NonNull;

public class TrackMetadata {
  @NonNull
  private String artistsName;

  @NonNull
  private String title;

  private String imageUrl;

  public TrackMetadata(@NonNull String artistsName, @NonNull String title, String imageUrl) {
    this.artistsName = artistsName;
    this.title = title;
    this.imageUrl = imageUrl;
  }

  @NonNull
  public String getArtistsName() {
    return artistsName;
  }

  @NonNull
  public String getTitle() {
    return title;
  }

  @NonNull
  public String getImageUrl() {
    return imageUrl;
  }
}
