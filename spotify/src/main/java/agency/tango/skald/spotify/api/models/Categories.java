package agency.tango.skald.spotify.api.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Categories {
  @SerializedName("href")
  private String href;

  @SerializedName("items")
  private List<Category> items;

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

  public List<Category> getItems() {
    return items;
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

  public Integer getTotal() {
    return total;
  }

  @Override
  public String toString() {
    return "Categories{" +
        "href='" + href + '\'' +
        ", items=" + items +
        ", limit=" + limit +
        ", next='" + next + '\'' +
        ", offset=" + offset +
        ", previous=" + previous +
        ", total=" + total +
        '}';
  }
}
