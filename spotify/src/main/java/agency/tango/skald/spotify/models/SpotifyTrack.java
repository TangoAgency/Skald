package agency.tango.skald.spotify.models;

import android.net.Uri;

import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.spotify.api.models.Track;

public class SpotifyTrack extends SkaldTrack {
  private SpotifyTrack(Uri uri) {
    super(uri);
  }

  public SpotifyTrack(Track track) {
    this(Uri.parse(String.format("skald://spotify/track/%s", track.getUri())));
  }
}
