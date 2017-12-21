package agency.tango.skald.deezer.models;

import agency.tango.skald.core.models.SkaldImage;

public class DeezerImage extends SkaldImage {
  private String imageUrl;

  public DeezerImage(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getImageUrl() {
    return imageUrl;
  }
}
