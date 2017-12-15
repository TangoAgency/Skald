package agency.tango.skald.core.models;

public class TrackMetadata {
  private final String artistsName;
  private final String title;
  private final SkaldImage image;

  public TrackMetadata(String artistsName, String title, SkaldImage image) {
    this.artistsName = artistsName;
    this.title = title;
    this.image = image;
  }

  public String getArtistsName() {
    return artistsName;
  }

  public String getTitle() {
    return title;
  }

  public SkaldImage getImage() {
    return image;
  }
}
