package agency.tango.skald.core;

import java.util.List;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;

public interface SearchService {
  Single<List<SkaldTrack>> searchForTracks(@NonNull String query);

  Single<List<SkaldPlaylist>> searchForPlaylists(@NonNull String query);
}
