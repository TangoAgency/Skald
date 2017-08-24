package agency.tango.spotify.models;

import com.google.gson.annotations.SerializedName;

public class Icon {
  @SerializedName("height")
  private Integer height;

  @SerializedName("url")
  private String url;

  @SerializedName("width")
  private Integer width;

  public Integer getHeight() {
    return height;
  }

  public String getUrl() {
    return url;
  }

  public Integer getWidth() {
    return width;
  }
}
