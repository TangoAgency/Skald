package agency.tango.skald.deezer.models;

import android.net.Uri;
import com.deezer.sdk.model.Playlist;
import agency.tango.skald.core.models.SkaldPlaylist;

public class DeezerPlaylist extends SkaldPlaylist {
  public DeezerPlaylist(Uri uri, String name, String imageUrl) {
    super(uri, name, imageUrl);
  }

  public DeezerPlaylist(Playlist playlist) {
    this(Uri.parse(String.format("skald://deezer/playlist/%s", playlist.getId())),
        playlist.getTitle(), playlist.getPictureUrl());
  }
}
