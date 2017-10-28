package agency.tango.skald.spotify.api.models;

import com.google.gson.annotations.SerializedName;

public class ExternalUrls {
  @SerializedName("spotify")
  private String spotify;

  public String getSpotify() {
    return spotify;
  }
}
