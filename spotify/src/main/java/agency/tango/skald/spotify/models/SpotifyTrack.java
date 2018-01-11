package agency.tango.skald.spotify.models;

import android.net.Uri;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.spotify.api.models.Track;

public class SpotifyTrack extends SkaldTrack {
  public SpotifyTrack(Uri uri, String artistName, String title, SpotifyImage spotifyImage) {
    super(uri, artistName, title, spotifyImage);
  }

  public SpotifyTrack(Track track) {
    this(Uri.parse(String.format("skald://spotify/track/%s", track.getUri())),
        track.getArtists().get(0).getName(), track.getTitle(),
        new SpotifyImage(track.getAlbum().getImages().get(0).getUrl()));
  }
}
