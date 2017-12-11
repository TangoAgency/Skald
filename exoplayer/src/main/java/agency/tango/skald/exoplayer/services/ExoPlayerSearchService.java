package agency.tango.skald.exoplayer.services;

import java.util.ArrayList;
import java.util.List;

import agency.tango.skald.core.SearchService;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import io.reactivex.Single;
import io.reactivex.functions.Function;

public class ExoPlayerSearchService implements SearchService {

  private final List<SearchService> searchServices = new ArrayList<>();

  @Override
  public Single<List<SkaldTrack>> searchForTracks(String query) {
    List<Single<List<SkaldTrack>>> tracks = new ArrayList<>();
    for (SearchService searchService : searchServices) {
      tracks.add(searchService.searchForTracks(query));
    }
    return mergeLists(tracks);
  }

  @Override
  public Single<List<SkaldPlaylist>> searchForPlaylists(String query) {
    List<Single<List<SkaldPlaylist>>> playlists = new ArrayList<>();
    for (SearchService searchService : searchServices) {
      playlists.add(searchService.searchForPlaylists(query));
    }
    return mergeLists(playlists);
  }

  public void add(SearchService searchService) {
    searchServices.add(searchService);
  }

  public void remove(SearchService searchService) {
    searchServices.remove(searchService);
  }

  private <T> Single<List<T>> mergeLists(List<Single<List<T>>> tracks) {
    return Single.merge(tracks)
        .flatMapIterable(new Function<List<T>, Iterable<T>>() {
          @Override
          public Iterable<T> apply(List<T> skaldTracks) throws Exception {
            return skaldTracks;
          }
        })
        .toList();
  }
}
