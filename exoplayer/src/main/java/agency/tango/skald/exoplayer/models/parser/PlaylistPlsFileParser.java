package agency.tango.skald.exoplayer.models.parser;

import android.net.Uri;

public class PlaylistPlsFileParser extends SkaldPlaylistFileParser {
  private static final String PLAYLIST_PLS = "audio/x-scpls";

  @Override
  public boolean canRead(String mimeType) {
    return mimeType.equals(PLAYLIST_PLS);
  }

  @Override
  public String getMimeType() {
    return PLAYLIST_PLS;
  }

  @Override
  protected boolean isUriLine(String line) {
    return line.contains("File");
  }

  @Override
  protected Uri getTrackUri(String line) {
    int startingIndex = line.indexOf("=") + 1;
    return Uri.parse(line.substring(startingIndex));
  }
}
