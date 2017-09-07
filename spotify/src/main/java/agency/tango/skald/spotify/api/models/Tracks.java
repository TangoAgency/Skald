package agency.tango.skald.spotify.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Tracks {
  @SerializedName("href")
  private String href;

  @SerializedName("items")
  private List<Track> tracks;

  @SerializedName("limit")
  private Integer limit;

  @SerializedName("next")
  private String next;

  @SerializedName("offset")
  private Integer offset;

  @SerializedName("previous")
  private Tracks previous;

  @SerializedName("total")
  private Integer total;

  public String getHref() {
    return href;
  }

  public Integer getTotal() {
    return total;
  }
}
