package agency.tango.skald.deezer.models;

import android.net.Uri;

import agency.tango.skald.core.models.SkaldTrack;

public class DeezerTrack extends SkaldTrack {
  public DeezerTrack(Uri uri, String artistName, String title) {
    super(uri, artistName, title);
  }
}
