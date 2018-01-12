package agency.tango.skald.exoplayer.models.parser;

public class PlaylistM3uFileParser extends SkaldPlaylistFileParser {
  private static final String PLAYLIST_M3U = "audio/mpegurl";

  @Override
  public boolean canRead(String mimeType) {
    return mimeType.equals(PLAYLIST_M3U);
  }

  @Override
  public String getMimeType() {
    return PLAYLIST_M3U;
  }

  @Override
  protected boolean isUriLine(String line) {
    return !line.startsWith("#") && line.contains("/");
  }
}
