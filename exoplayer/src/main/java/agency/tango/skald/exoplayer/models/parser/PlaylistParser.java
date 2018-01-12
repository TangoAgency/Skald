package agency.tango.skald.exoplayer.models.parser;

import android.net.Uri;
import java.util.List;

public abstract class PlaylistParser {
  public abstract boolean canRead(String mimeType);

  public abstract List<Uri> getTracksUris(Uri uri);

  public abstract String getMimeType();
}
