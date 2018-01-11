package agency.tango.skald.exoplayer.models;

import android.net.Uri;
import java.util.List;
import agency.tango.skald.core.models.SkaldPlaylist;

public class ExoPlayerPlaylist extends SkaldPlaylist {
  private final List<ExoPlayerTrack> tracks;

  public ExoPlayerPlaylist(Uri uri, String name, String imageUrl, List<ExoPlayerTrack> tracks) {
    super(uri, name, imageUrl);
    this.tracks = tracks;
  }

  public List<ExoPlayerTrack> getTracks() {
    return tracks;
  }
}
