package agency.tango.skald.spotify.models;

import android.net.Uri;

import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.spotify.api.models.Track;

public class SpotifyTrack extends SkaldTrack {
  public SpotifyTrack(Track track) {
    this(Uri.parse(String.format("skald://spotify/track/%s", track.getUri())),
        track.getAlbum().getImages().get(0).getUrl(), track.getArtists().get(0).getName(),
        track.getTitle());
  }

  private SpotifyTrack(Uri uri, String imageUrl, String artistName, String title) {
    super(uri, imageUrl, artistName, title);
  }
}
