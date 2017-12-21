package agency.tango.skald.spotify.models;

import agency.tango.skald.core.models.SkaldImage;

public class SpotifyImage extends SkaldImage {
  private String imageUrl;

  public SpotifyImage(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getImageUrl() {
    return imageUrl;
  }
}
