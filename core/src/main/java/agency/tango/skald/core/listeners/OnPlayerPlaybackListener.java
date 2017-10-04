package agency.tango.skald.core.listeners;

import java.util.List;

import agency.tango.skald.core.errors.PlaybackError;
import agency.tango.skald.core.models.TrackMetadata;

public class OnPlayerPlaybackListener implements OnPlaybackListener {
  private List<OnPlaybackListener> onPlaybackListeners;

  public OnPlayerPlaybackListener(List<OnPlaybackListener> onPlaybackListeners) {
    this.onPlaybackListeners = onPlaybackListeners;
  }

  @Override
    public void onPlayEvent(TrackMetadata trackMetadata) {
      for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
        onPlaybackListener.onPlayEvent(trackMetadata);
      }
    }

    @Override
    public void onPauseEvent() {
      for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
        onPlaybackListener.onPauseEvent();
      }
    }

    @Override
    public void onResumeEvent() {
      for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
        onPlaybackListener.onResumeEvent();
      }
    }

    @Override
    public void onStopEvent() {
      for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
        onPlaybackListener.onStopEvent();
      }
    }

    @Override
    public void onError(PlaybackError playbackError) {
      for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
        onPlaybackListener.onError(playbackError);
      }
    }
}
