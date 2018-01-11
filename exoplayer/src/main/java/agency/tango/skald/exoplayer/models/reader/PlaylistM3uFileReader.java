package agency.tango.skald.exoplayer.models.reader;

import android.net.Uri;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import agency.tango.skald.exoplayer.models.ExoPlayerTrack;

public class PlaylistM3uFileReader extends PlaylistReader {
  private static final String PLAYLIST_M3U = "audio/mpegurl";

  @Override
  public boolean canRead(String mimeType) {
    return mimeType.equals(PLAYLIST_M3U);
  }

  @Override
  public List<ExoPlayerTrack> getTracks(Uri uri) {
    try {
      List<Uri> trackUris = readTrackUrisFromFile(
          new File(new URI(uri.toString())));

      List<ExoPlayerTrack> tracks = new ArrayList<>();
      for (Uri trackUri : trackUris) {
        tracks.add(new ExoPlayerTrack(trackUri, "", "", null));
      }
      return tracks;
    } catch (IOException exception) {
      //TODO handle exceptions
      exception.printStackTrace();
    } catch (URISyntaxException exception) {
      exception.printStackTrace();
    }
    return null;
  }

  @Override
  public String getMimeType() {
    return PLAYLIST_M3U;
  }

  private List<Uri> readTrackUrisFromFile(File file) throws IOException {
    List<Uri> trackUris = new ArrayList<>();
    BufferedReader br = new BufferedReader(new FileReader(file));

    String line;
    while ((line = br.readLine()) != null) {
      if (isUriLine(line)) {
        trackUris.add(Uri.parse(line));
      }
    }
    br.close();

    return trackUris;
  }

  private boolean isUriLine(String line) {
    return !line.startsWith("#") && line.contains("/");
  }
}
