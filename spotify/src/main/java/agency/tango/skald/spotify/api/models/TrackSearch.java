package agency.tango.skald.spotify.api.models;

import com.google.gson.annotations.SerializedName;

public class TrackSearch {
  @SerializedName("tracks")
  private Tracks tracks;

  public Tracks getTracks() {
    return tracks;
  }
}
