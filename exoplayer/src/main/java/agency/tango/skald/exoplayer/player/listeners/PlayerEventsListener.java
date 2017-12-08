package agency.tango.skald.exoplayer.player.listeners;

import android.media.session.PlaybackState;
import android.os.Handler;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

import java.util.List;

import agency.tango.skald.core.listeners.OnErrorListener;
import agency.tango.skald.core.listeners.OnLoadingListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.models.TrackMetadata;

public class PlayerEventsListener implements Player.EventListener {
  private final Handler mainHandler;
  private final List<OnPlaybackListener> onPlaybackListeners;
  private final List<OnLoadingListener> onLoadingListeners;
  private final OnErrorListener onErrorListener;
  private final SimpleExoPlayer exoPlayer;

  public PlayerEventsListener(Handler mainHandler, List<OnPlaybackListener> onPlaybackListeners,
      List<OnLoadingListener> onLoadingListeners, OnErrorListener onErrorListener,
      SimpleExoPlayer exoPlayer) {
    this.mainHandler = mainHandler;
    this.onPlaybackListeners = onPlaybackListeners;
    this.onLoadingListeners = onLoadingListeners;
    this.onErrorListener = onErrorListener;
    this.exoPlayer = exoPlayer;
  }

  @Override
  public void onTimelineChanged(Timeline timeline, Object manifest) {

  }

  @Override
  public void onTracksChanged(TrackGroupArray trackGroups,
      TrackSelectionArray trackSelections) {
    if (trackGroups.length > 0) {
      notifyPlayEvent();
    }
  }

  @Override
  public void onLoadingChanged(boolean isLoading) {
    if (isLoading && exoPlayer.getPlaybackState()!=PlaybackState.STATE_PLAYING) {
      notifyLoadingEvent();
    }
  }

  @Override
  public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
    if (playbackState == PlaybackState.STATE_PLAYING) {
      if (playWhenReady) {
        notifyResumeEvent();
      } else {
        notifyPauseEvent();
      }
    } else if (playbackState == PlaybackState.STATE_PAUSED) {
      notifyPauseEvent();
    } else if (playbackState == PlaybackState.STATE_STOPPED) {
      notifyStopEvent();
    }
  }

  @Override
  public void onRepeatModeChanged(int repeatMode) {

  }

  @Override
  public void onPlayerError(ExoPlaybackException error) {
    onErrorListener.onError(error);
  }

  @Override
  public void onPositionDiscontinuity() {

  }

  @Override
  public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

  }

  private void notifyStopEvent() {
    mainHandler.post(new Runnable() {
      @Override
      public void run() {
        for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
          onPlaybackListener.onStopEvent();
        }
      }
    });
  }

  private void notifyPauseEvent() {
    mainHandler.post(new Runnable() {
      @Override
      public void run() {
        for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
          onPlaybackListener.onPauseEvent();
        }
      }
    });
  }

  private void notifyResumeEvent() {
    mainHandler.post(new Runnable() {
      @Override
      public void run() {
        for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
          onPlaybackListener.onResumeEvent();
        }
      }
    });
  }

  private void notifyPlayEvent() {
    mainHandler.post(new Runnable() {
      @Override
      public void run() {
        for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
          onPlaybackListener.onPlayEvent(new TrackMetadata("", "", null));
        }
      }
    });
  }

  private void notifyLoadingEvent() {
    mainHandler.post(new Runnable() {
      @Override
      public void run() {
        for (OnLoadingListener onLoadingListener : onLoadingListeners) {
          onLoadingListener.onLoading();
        }
      }
    });
  }
}
