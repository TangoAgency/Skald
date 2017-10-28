package agency.tango.skald.spotify.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Category {
  @SerializedName("href")
  private String href;

  @SerializedName("icons")
  private List<Icon> icons;

  @SerializedName("id")
  private String id;

  @SerializedName("name")
  private String name;

  public String getHref() {
    return href;
  }

  public List<Icon> getIcons() {
    return icons;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "Category{" +
        "href='" + href + '\'' +
        ", icons=" + icons +
        ", id='" + id + '\'' +
        ", name='" + name + '\'' +
        '}';
  }
}
