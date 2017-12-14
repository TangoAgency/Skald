package agency.tango.skald.core.models;

public class TrackMetadata {
  private String artistsName;
  private String title;
  private String imageUrl;
  private byte[] pictureData;

  public TrackMetadata(String artistsName, String title, String imageUrl) {
    this.artistsName = artistsName;
    this.title = title;
    this.imageUrl = imageUrl;
  }

  public TrackMetadata(String artistsName, String title, byte[] pictureData) {
    this.artistsName = artistsName;
    this.title = title;
    this.pictureData = pictureData;
  }

  public String getArtistsName() {
    return artistsName;
  }

  public String getTitle() {
    return title;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public byte[] getPictureData() {
    return pictureData;
  }
}
