package agency.tango.skald.spotify.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Playlist {
  @SerializedName("collaborative")
  private Boolean collaborative;

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

  @SerializedName("owner")
  private Owner owner;

  @SerializedName("public")
  private Boolean isPublic;

  @SerializedName("snapshot_id")
  private String snapshotId;

  @SerializedName("tracks")
  private Tracks tracks;

  @SerializedName("type")
  private String type;

  @SerializedName("uri")
  private String uri;

  public Boolean getCollaborative() {
    return collaborative;
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

  public Owner getOwner() {
    return owner;
  }

  public Boolean getIsPublic() {
    return isPublic;
  }

  public String getSnapshotId() {
    return snapshotId;
  }

  public Tracks getTracks() {
    return tracks;
  }

  public String getType() {
    return type;
  }

  public String getUri() {
    return uri;
  }
}
