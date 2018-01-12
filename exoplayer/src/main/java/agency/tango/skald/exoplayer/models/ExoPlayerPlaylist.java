package agency.tango.skald.exoplayer.models;

import android.net.Uri;
import java.util.List;
import agency.tango.skald.core.models.SkaldPlaylist;

public class ExoPlayerPlaylist extends SkaldPlaylist {
  private final List<Uri> tracksUris;

  public ExoPlayerPlaylist(Uri uri, String name, ExoPlayerImage image,
      List<Uri> tracksUris) {
    super(uri, name, image);
    this.tracksUris = tracksUris;
  }

  public List<Uri> getTracksUris() {
    return tracksUris;
  }
}
