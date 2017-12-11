package agency.tango.skald.spotify.api.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Track {
  @SerializedName("album")
  private Album album;

  @SerializedName("artists")
  private List<Artist> artists;

  @SerializedName("available_markets")
  private List<String> availableMarkets;

  @SerializedName("disc_number")
  private Integer discNumber;

  @SerializedName("duration_ms")
  private Integer durationMs;

  @SerializedName("explicit")
  private Boolean explicit;

  @SerializedName("external_urls")
  private ExternalUrls externalUrls;

  @SerializedName("href")
  private String href;

  @SerializedName("id")
  private String id;

  @SerializedName("name")
  private String title;

  @SerializedName("popularity")
  private Integer popularity;

  @SerializedName("preview_url")
  private String previewUrl;

  @SerializedName("track_number")
  private Integer trackNumber;

  @SerializedName("type")
  private String type;

  @SerializedName("uri")
  private String uri;

  public Album getAlbum() {
    return album;
  }

  public List<Artist> getArtists() {
    return artists;
  }

  public List<String> getAvailableMarkets() {
    return availableMarkets;
  }

  public Integer getDiscNumber() {
    return discNumber;
  }

  public Integer getDurationMs() {
    return durationMs;
  }

  public Boolean getExplicit() {
    return explicit;
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

  public String getTitle() {
    return title;
  }

  public Integer getPopularity() {
    return popularity;
  }

  public String getPreviewUrl() {
    return previewUrl;
  }

  public Integer getTrackNumber() {
    return trackNumber;
  }

  public String getType() {
    return type;
  }

  public String getUri() {
    return uri;
  }
}
