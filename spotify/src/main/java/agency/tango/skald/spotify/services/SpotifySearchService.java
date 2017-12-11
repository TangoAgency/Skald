package agency.tango.skald.spotify.services;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import agency.tango.skald.core.SearchService;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.spotify.api.SpotifyApi;
import agency.tango.skald.spotify.api.models.BrowsePlaylists;
import agency.tango.skald.spotify.api.models.Playlist;
import agency.tango.skald.spotify.api.models.Track;
import agency.tango.skald.spotify.api.models.TrackSearch;
import agency.tango.skald.spotify.models.SpotifyPlaylist;
import agency.tango.skald.spotify.models.SpotifyTrack;
import io.reactivex.Single;

public class SpotifySearchService implements SearchService {
  private static final String TRACK_TYPE = "track";
  private static final String PLAYLIST_TYPE = "playlist";

  private final SpotifyApi.SpotifyApiImpl spotifyApi;

  public SpotifySearchService(SpotifyApi.SpotifyApiImpl spotifyApi) {
    this.spotifyApi = spotifyApi;
  }

  @Override
  public Single<List<SkaldTrack>> searchForTracks(final String query) {
    return spotifyApi.getTracksForQuery(query, TRACK_TYPE)
        .map(this::mapSpotifyTracksToSkaldTracks);
  }

  @Override
  public Single<List<SkaldPlaylist>> searchForPlaylists(final String query) {
    return spotifyApi.getPlaylistsForQuery(query, PLAYLIST_TYPE)
        .map(this::mapSpotifyPlaylistsToSkaldPlaylists);
  }

  @NonNull
  private List<SkaldTrack> mapSpotifyTracksToSkaldTracks(TrackSearch searchTrack) {
    List<SkaldTrack> skaldTracks = new ArrayList<>();
    for (Track track : searchTrack.getTracks().getItems()) {
      skaldTracks.add(new SpotifyTrack(track));
    }
    return skaldTracks;
  }

  @NonNull
  private List<SkaldPlaylist> mapSpotifyPlaylistsToSkaldPlaylists(BrowsePlaylists browsePlaylists) {
    List<SkaldPlaylist> skaldPlaylists = new ArrayList<>();
    for (Playlist playlist : browsePlaylists.getPlaylists().getItems()) {
      skaldPlaylists.add(new SpotifyPlaylist(playlist));
    }
    return skaldPlaylists;
  }
}
