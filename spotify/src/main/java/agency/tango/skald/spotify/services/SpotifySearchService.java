package agency.tango.skald.spotify.services;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import agency.tango.skald.core.SearchService;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.spotify.api.models.BrowsePlaylists;
import agency.tango.skald.spotify.api.models.Playlist;
import agency.tango.skald.spotify.api.models.Tokens;
import agency.tango.skald.spotify.api.models.Track;
import agency.tango.skald.spotify.api.models.TrackSearch;
import agency.tango.skald.spotify.authentication.SpotifyAuthData;
import agency.tango.skald.spotify.models.SpotifyPlaylist;
import agency.tango.skald.spotify.models.SpotifyTrack;
import agency.tango.skald.spotify.provider.SpotifyProvider;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;

public class SpotifySearchService extends SpotifyService implements SearchService {
  private static final String TRACK_TYPE = "track";
  private static final String PLAYLIST_TYPE = "playlist";

  public SpotifySearchService(Context context, SpotifyAuthData spotifyAuthData,
      SpotifyProvider spotifyProvider) {
    super(context, spotifyAuthData, spotifyProvider);
  }

  @Override
  public Single<List<SkaldTrack>> searchForTracks(final String query) {
    return spotifyApi.getTracksForQuery(query, TRACK_TYPE)
        .onErrorResumeNext(new Function<Throwable, SingleSource<? extends TrackSearch>>() {
          @Override
          public SingleSource<? extends TrackSearch> apply(Throwable throwable) throws Exception {
            if (isTokenExpired(throwable)) {
              return refreshToken()
                  .flatMap(new Function<Tokens, SingleSource<TrackSearch>>() {
                    @Override
                    public SingleSource<TrackSearch> apply(Tokens tokens) throws Exception {
                      saveTokens(tokens);

                      return spotifyApi.getTracksForQuery(query, TRACK_TYPE);
                    }
                  });
            }

            return Single.just(new TrackSearch());
          }
        })
        .map(new Function<TrackSearch, List<SkaldTrack>>() {
          @Override
          public List<SkaldTrack> apply(TrackSearch searchTrack) throws Exception {
            return mapSpotifyTracksToSkaldTracks(searchTrack);
          }
        });
  }

  @Override
  public Single<List<SkaldPlaylist>> searchForPlaylists(final String query) {
    return spotifyApi.getPlaylistsForQuery(query, PLAYLIST_TYPE)
        .onErrorResumeNext(new Function<Throwable, SingleSource<? extends BrowsePlaylists>>() {
          @Override
          public SingleSource<? extends BrowsePlaylists> apply(Throwable throwable)
              throws Exception {
            if (isTokenExpired(throwable)) {
              return refreshToken()
                  .flatMap(new Function<Tokens, SingleSource<BrowsePlaylists>>() {
                    @Override
                    public SingleSource<BrowsePlaylists> apply(Tokens tokens) throws Exception {
                      saveTokens(tokens);

                      return spotifyApi.getPlaylistsForQuery(query, PLAYLIST_TYPE);
                    }
                  });
            }

            return Single.just(new BrowsePlaylists());
          }
        })
        .map(new Function<BrowsePlaylists, List<SkaldPlaylist>>() {
          @Override
          public List<SkaldPlaylist> apply(BrowsePlaylists browsePlaylists) throws Exception {
            return mapSpotifyPlaylistsToSkaldPlaylists(browsePlaylists);
          }
        });
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
