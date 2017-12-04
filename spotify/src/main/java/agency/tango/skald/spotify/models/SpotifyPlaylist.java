package agency.tango.skald.spotify.models;

import android.net.Uri;

import java.util.List;

import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.spotify.api.models.Image;
import agency.tango.skald.spotify.api.models.Playlist;

public class SpotifyPlaylist extends SkaldPlaylist {
  public SpotifyPlaylist(Playlist playlist) {
    this(Uri.parse(String.format("skald://spotify/playlist/%s", playlist.getUri())),
        getImageUrl(playlist), playlist.getName());
  }

  private SpotifyPlaylist(Uri uri, String imageUrl, String name) {
    super(uri, imageUrl, name);
  }

  private static String getImageUrl(Playlist playlist) {
    List<Image> images = playlist.getImages();
    return !images.isEmpty() ? images.get(0).getUrl() : "";
  }
}
