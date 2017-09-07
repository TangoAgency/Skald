package agency.tango.skald.core;

import java.util.List;

import agency.tango.skald.core.models.SkaldTrack;

public interface ApiCalls {
  List<SkaldTrack> searchForTracks(String query);
}
