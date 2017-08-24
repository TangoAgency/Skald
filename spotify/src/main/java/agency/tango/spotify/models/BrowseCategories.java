package agency.tango.spotify.models;

import com.google.gson.annotations.SerializedName;

public class BrowseCategories {
  @SerializedName("categories")
  private Categories categories;

  public Categories getCategories() {
    return categories;
  }

  @Override
  public String toString() {
    return "BrowseCategories{" +
        "categories=" + categories.toString() +
        '}';
  }
}
