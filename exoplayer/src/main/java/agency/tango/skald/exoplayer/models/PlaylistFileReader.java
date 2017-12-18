package agency.tango.skald.exoplayer.models;

import android.net.Uri;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistFileReader {
  public static List<Uri> readTrackUrisFromFile(File file) throws IOException {
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

  private static boolean isUriLine(String line) {
    return !line.startsWith("#") && line.contains("/");
  }
}
