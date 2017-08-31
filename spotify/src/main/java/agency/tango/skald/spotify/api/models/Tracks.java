package agency.tango.skald.spotify.api.models;

import com.google.gson.annotations.SerializedName;

public class Tracks {
  @SerializedName("href")
  private String href;

  @SerializedName("total")
  private Integer total;

  public String getHref() {
    return href;
  }

  public Integer getTotal() {
    return total;
  }
}
