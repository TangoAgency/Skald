package agency.tango.spotify.models;

import com.google.gson.annotations.SerializedName;

public class Owner {
  @SerializedName("external_urls")
  private ExternalUrls externalUrls;

  @SerializedName("href")
  private String href;

  @SerializedName("id")
  private String id;

  @SerializedName("type")
  private String type;

  @SerializedName("uri")
  private String uri;

  public ExternalUrls getExternalUrls() {
    return externalUrls;
  }

  public String getHref() {
    return href;
  }

  public String getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public String getUri() {
    return uri;
  }
}
