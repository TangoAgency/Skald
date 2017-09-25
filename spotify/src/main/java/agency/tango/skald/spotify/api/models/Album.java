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

  public String getAlbumType() {
    return albumType;
  }

  public List<Artist> getArtists() {
    return artists;
  }

  public List<String> getAvailableMarkets() {
    return availableMarkets;
  }

  public ExternalUrls getExternalUrls() {
    return externalUrls;
  }

  public String getHref() {
    return href;
  }

  public String getId() {
    return id;
  }

  public List<Image> getImages() {
    return images;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getUri() {
    return uri;
  }
}
