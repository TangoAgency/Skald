package agency.tango.skald.core.models;

public class ServiceImage extends SkaldImage {
  private String imageUrl;

  public ServiceImage(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getImageUrl() {
    return imageUrl;
  }
}
