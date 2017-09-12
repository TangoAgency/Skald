package agency.tango.skald.core.models;

public class TrackMetadata {
  private String artistsName;
  private String title;

  public TrackMetadata(String artistsName, String title) {
    this.artistsName = artistsName;
    this.title = title;
  }

  public String getArtistsName() {
    return artistsName;
  }

  public String getTitle() {
    return title;
  }
}
