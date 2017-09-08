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
  private String previous;

  @SerializedName("total")
  private Integer total;

  public String getHref() {
    return href;
  }

  public Integer getTotal() {
    return total;
  }

  public List<Track> getTracks() {
    return tracks;
  }

  public Integer getLimit() {
    return limit;
  }

  public String getNext() {
    return next;
  }

  public Integer getOffset() {
    return offset;
  }

  public String getPrevious() {
    return previous;
  }

  @Override
  public String toString() {
    return "Tracks{" +
        "href='" + href + '\'' +
        ", tracks=" + tracks +
        ", limit=" + limit +
        ", next='" + next + '\'' +
        ", offset=" + offset +
        ", previous=" + previous +
        ", total=" + total +
        '}';
  }
}
