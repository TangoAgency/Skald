package agency.tango.skald.core.models;

public class TrackMetadata {
  private String artistName;
  private String title;
  private String imageUrl;

  public TrackMetadata(String artistName, String title, String imageUrl) {
    this.artistName = artistName;
    this.title = title;
    this.imageUrl = imageUrl;
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
    return "artist name='" + artistName + '\'' +
        ", title='" + title + '\'';
  }
}
