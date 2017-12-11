package agency.tango.skald.deezer.models;

import android.net.Uri;
import com.deezer.sdk.model.Track;
import agency.tango.skald.core.models.SkaldTrack;

public class DeezerTrack extends SkaldTrack {
  public DeezerTrack(Track track) {
    this(Uri.parse(String.format("skald://deezer/track/%s", track.getId())),
        track.getArtist().getName(), track.getTitle(), track.getAlbum().getImageUrl());
  }

  public DeezerTrack(Uri uri, String artistName, String title, String imageUrl) {
    super(uri, artistName, title, imageUrl);
  }
}
