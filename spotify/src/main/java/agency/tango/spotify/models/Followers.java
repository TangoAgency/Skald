package agency.tango.spotify.models;

import com.google.gson.annotations.SerializedName;

public class Followers {
  //TODO check if type is okay
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
