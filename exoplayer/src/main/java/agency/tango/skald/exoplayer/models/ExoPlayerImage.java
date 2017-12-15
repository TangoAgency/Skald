package agency.tango.skald.exoplayer.models;

import agency.tango.skald.core.models.SkaldImage;

public class ExoPlayerImage extends SkaldImage {
  private byte[] pictureData;

  public ExoPlayerImage(byte[] pictureData) {
    this.pictureData = pictureData;
  }

  public byte[] getPictureData() {
    return pictureData;
  }
}
