package agency.tango.skald.exoplayer.models;

import android.net.Uri;

import agency.tango.skald.core.models.SkaldTrack;

public class ExoPlayerTrack extends SkaldTrack {
  public ExoPlayerTrack(Uri uri, String artistName, String title, String imageUrl) {
    super(uri, artistName, title, imageUrl);
  }
}
