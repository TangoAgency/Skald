package agency.tango.skald.core.listeners;

import agency.tango.skald.core.errors.PlaybackError;
import agency.tango.skald.core.models.TrackMetadata;

public interface OnPlaybackListener {
  void onPlayEvent(TrackMetadata trackMetadata);

  void onPauseEvent();

  void onResumeEvent();

  void onStopEvent();

  void onError(PlaybackError playbackError);
}
