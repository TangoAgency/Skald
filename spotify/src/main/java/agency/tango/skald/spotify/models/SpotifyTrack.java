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
        getImageUrl(track), getArtistName(track),
        track.getTitle());
  }

  private static String getArtistName(Track track) {
    return getArtistNameIfListIsNotEmpty(track.getArtists());
  }

  private static String getImageUrl(Track track) {
    return getImageUrlIfListIsNotEmpty(track.getAlbum().getImages());
  }

  private static String getImageUrlIfListIsNotEmpty(List<Image> images) {
    return !images.isEmpty() ? images.get(0).getUrl() : EMPTY;
  }

  private static String getArtistNameIfListIsNotEmpty(List<Artist> artists) {
    return !artists.isEmpty() ? artists.get(0).getName() : EMPTY;
  }
}
