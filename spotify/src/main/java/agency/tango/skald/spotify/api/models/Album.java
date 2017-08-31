package agency.tango.skald.spotify.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Album {
  @SerializedName("album_type")
  private String albumType;

  @SerializedName("artists")
  private List<Artist> artists;

  @SerializedName("available_markets")
  private List<String> availableMarkets;

  @SerializedName("external_urls")
  private ExternalUrls externalUrls;

  @SerializedName("href")
  private String href;

  @SerializedName("id")
  private String id;

  @SerializedName("images")
  private List<Image> images;

  @SerializedName("name")
  private String name;

  @SerializedName("type")
  private String type;

  @SerializedName("uri")
  private String uri;

}
