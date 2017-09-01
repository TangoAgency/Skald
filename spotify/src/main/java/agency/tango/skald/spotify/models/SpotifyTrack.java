package agency.tango.skald.spotify.models;

import android.net.Uri;

import agency.tango.skald.core.models.SkaldTrack;

public class SpotifyTrack extends SkaldTrack {

  //SpotifyTrack(Track track) {
  //  this(track.getUri());
  //}

  public SpotifyTrack(Uri uri) {
    super(uri);
  }
}
