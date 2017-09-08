package agency.tango.skald.core;

import agency.tango.skald.core.models.SkaldTrack;
import io.reactivex.Observable;

public interface ApiCalls {
  Observable<SkaldTrack> searchForTracks(String query);
}
