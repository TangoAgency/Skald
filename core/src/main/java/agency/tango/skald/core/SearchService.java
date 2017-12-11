package agency.tango.skald.core;

import java.util.List;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import io.reactivex.Single;

public interface SearchService {
  Single<List<SkaldTrack>> searchForTracks(String query);

  Single<List<SkaldPlaylist>> searchForPlaylists(String query);
}
