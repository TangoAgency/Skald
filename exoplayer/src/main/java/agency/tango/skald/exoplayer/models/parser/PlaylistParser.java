package agency.tango.skald.exoplayer.models.parser;

import android.net.Uri;
import java.util.List;
import agency.tango.skald.exoplayer.models.ExoPlayerTrack;

public abstract class PlaylistParser {
  public abstract boolean canRead(String mimeType);

  public abstract List<ExoPlayerTrack> getTracks(Uri uri);

  public abstract String getMimeType();
}
