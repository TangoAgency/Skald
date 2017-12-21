package agency.tango.skald.exoplayer.models;

import android.net.Uri;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.exoplayer.models.reader.PlaylistFileReader;

public class ExoPlayerPlaylist extends SkaldPlaylist {
  private final List<ExoPlayerTrack> tracks;

  public ExoPlayerPlaylist(Uri uri, String name, String imageUrl) {
    super(uri, name, imageUrl);
    tracks = getTracksFromFile();
  }

  public List<ExoPlayerTrack> getTracks() {
    return tracks;
  }

  private List<ExoPlayerTrack> getTracksFromFile() {
    try {
      List<Uri> trackUris = PlaylistFileReader.readTrackUrisFromFile(
          new File(new URI(getUri().toString())));

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
}
