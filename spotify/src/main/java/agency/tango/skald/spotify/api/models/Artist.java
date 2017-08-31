package agency.tango.skald.spotify.api.models;

import com.google.gson.annotations.SerializedName;

public class Artist {
  @SerializedName("external_urls")
  private ExternalUrls externalUrls;

  @SerializedName("href")
  private String href;

  @SerializedName("id")
  private String id;

  @SerializedName("name")
  private String name;

  @SerializedName("type")
  private String type;

  @SerializedName("uri")
  private String uri;
}
