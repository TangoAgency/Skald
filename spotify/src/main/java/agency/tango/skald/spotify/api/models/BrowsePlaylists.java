package agency.tango.skald.spotify.api.models;

import com.google.gson.annotations.SerializedName;

public class BrowsePlaylists {
  @SerializedName("playlists")
  private Playlists playlists;

  public Playlists getPlaylists() {
    return playlists;
  }

  @Override
  public String toString() {
    return "BrowsePlaylists{" +
        "playlists=" + playlists.toString() +
        '}';
  }
}
