package agency.tango.skald.spotify.models;

import android.net.Uri;
import java.util.List;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.spotify.api.models.Artist;
import agency.tango.skald.spotify.api.models.Image;
import agency.tango.skald.spotify.api.models.Track;

public class SpotifyTrack extends SkaldTrack {
  private static final String EMPTY = "";

  public SpotifyTrack(Track track) {
    super(Uri.parse(String.format("skald://spotify/track/%s", track.getUri())),
        getImageUrl(track), getArtistName(track), track.getTitle());
  }

  private static String getArtistName(Track track) {
    List<Artist> artists = track.getArtists();
    return !artists.isEmpty() ? artists.get(0).getName() : EMPTY;
  }

  private static String getImageUrl(Track track) {
    List<Image> images = track.getAlbum().getImages();
    return !images.isEmpty() ? images.get(0).getUrl() : EMPTY;
  }
}
