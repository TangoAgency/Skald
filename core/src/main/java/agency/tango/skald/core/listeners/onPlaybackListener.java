package agency.tango.skald.core.listeners;

import agency.tango.skald.core.models.TrackMetadata;

public interface onPlaybackListener {
  void onPlaybackEvent(TrackMetadata trackMetadata);
}
