package agency.tango.skald.exoplayer.models.parser;

import android.net.Uri;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import agency.tango.skald.exoplayer.models.ExoPlayerTrack;

public abstract class SkaldPlaylistFileParser extends PlaylistParser {
  private static final String TAG = SkaldPlaylistFileParser.class.getSimpleName();

  @Override
  public abstract boolean canRead(String mimeType);

  @Override
  public List<ExoPlayerTrack> getTracks(Uri uri) {
    try {
      List<Uri> lines = readTrackUrisFromFile(
          new File(new URI(uri.toString())));

      List<ExoPlayerTrack> tracks = new ArrayList<>();
      for (Uri trackUri : lines) {
        tracks.add(new ExoPlayerTrack(trackUri, "", "", null));
      }
      return tracks;
    } catch (IOException exception) {
      Log.e(TAG, exception.getMessage());
      exception.printStackTrace();
    } catch (URISyntaxException exception) {
      Log.e(TAG, String.format("Cannot parse playlist file uri : %s", exception.getMessage()));
      exception.printStackTrace();
    }
    return new ArrayList<>();
  }

  @Override
  public abstract String getMimeType();

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

  protected abstract boolean isUriLine(String line);
}
