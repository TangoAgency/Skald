package agency.tango.skald.exoplayer.player.listeners;

import android.media.session.PlaybackState;
import android.os.Handler;
import android.util.Log;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.emsg.EventMessage;
import com.google.android.exoplayer2.metadata.id3.ApicFrame;
import com.google.android.exoplayer2.metadata.id3.CommentFrame;
import com.google.android.exoplayer2.metadata.id3.GeobFrame;
import com.google.android.exoplayer2.metadata.id3.Id3Frame;
import com.google.android.exoplayer2.metadata.id3.PrivFrame;
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame;
import com.google.android.exoplayer2.metadata.id3.UrlLinkFrame;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import java.util.Arrays;
import java.util.List;
import agency.tango.skald.core.listeners.OnErrorListener;
import agency.tango.skald.core.listeners.OnLoadingListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.models.TrackMetadata;
import agency.tango.skald.exoplayer.models.ExoPlayerImage;

public class PlayerEventsListener implements Player.EventListener {
  private static final String TAG = PlayerEventsListener.class.getSimpleName();
  private final Handler mainHandler;
  private final List<OnPlaybackListener> onPlaybackListeners;
  private final List<OnLoadingListener> onLoadingListeners;
  private final OnErrorListener onErrorListener;
  private final SimpleExoPlayer exoPlayer;
  private final MappingTrackSelector trackSelector;

  public PlayerEventsListener(Handler mainHandler, List<OnPlaybackListener> onPlaybackListeners,
      List<OnLoadingListener> onLoadingListeners, OnErrorListener onErrorListener,
      SimpleExoPlayer exoPlayer, MappingTrackSelector trackSelector) {
    this.mainHandler = mainHandler;
    this.onPlaybackListeners = onPlaybackListeners;
    this.onLoadingListeners = onLoadingListeners;
    this.onErrorListener = onErrorListener;
    this.exoPlayer = exoPlayer;
    this.trackSelector = trackSelector;
  }

  @Override
  public void onTimelineChanged(Timeline timeline, Object manifest) {

  }

  @Override
  public void onTracksChanged(TrackGroupArray trackGroups,
      TrackSelectionArray trackSelections) {
    //if (trackGroups.length > 0) {
    //  notifyPlayEvent();
    //}

    TrackMetadata trackMetadata = null;
    MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
    if (mappedTrackInfo == null) {
      Log.d(TAG, "Tracks []");
      return;
    }
    Log.d(TAG, "Tracks [");
    // Log tracks associated to renderers.
    for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.length; rendererIndex++) {
      TrackGroupArray rendererTrackGroups = mappedTrackInfo.getTrackGroups(rendererIndex);
      TrackSelection trackSelection = trackSelections.get(rendererIndex);

      if (rendererTrackGroups.length > 0) {
        if (trackSelection != null) {
          for (int selectionIndex = 0; selectionIndex < trackSelection.length(); selectionIndex++) {
            Metadata metadata = trackSelection.getFormat(selectionIndex).metadata;
            if (metadata != null) {
              Log.d(TAG, "    Metadata [");
              trackMetadata = printAndGetMetadata(metadata, "      ");
              Log.d(TAG, "    ]");
              break;
            }
          }
        }
        Log.d(TAG, "  ]");
      }
    }

    notifyPlayEvent(trackMetadata);
  }

  @Override
  public void onLoadingChanged(boolean isLoading) {
    if (isLoading && exoPlayer.getPlaybackState() != PlaybackState.STATE_PLAYING) {
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

  private void notifyPlayEvent(final TrackMetadata trackMetadata) {
    mainHandler.post(new Runnable() {
      @Override
      public void run() {
        for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
          onPlaybackListener.onPlayEvent(trackMetadata);
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

  private TrackMetadata printAndGetMetadata(Metadata metadata, String prefix) {
    String artistName = "";
    String title = "";
    byte[] pictureData = null;

    for (int i = 0; i < metadata.length(); i++) {
      Metadata.Entry entry = metadata.get(i);
      if (entry instanceof TextInformationFrame) {
        TextInformationFrame textInformationFrame = (TextInformationFrame) entry;
        Log.d(TAG, prefix + String.format("%s: value=%s", textInformationFrame.id,
            textInformationFrame.value));

        if (textInformationFrame.id.equals("TIT2")) {
          title = textInformationFrame.value;
        } else if (textInformationFrame.id.equals("TPE1")) {
          artistName = textInformationFrame.value;
        }

      } else if (entry instanceof UrlLinkFrame) {
        UrlLinkFrame urlLinkFrame = (UrlLinkFrame) entry;
        Log.d(TAG, prefix + String.format("%s: url=%s", urlLinkFrame.id, urlLinkFrame.url));
      } else if (entry instanceof PrivFrame) {
        PrivFrame privFrame = (PrivFrame) entry;
        Log.d(TAG, prefix + String.format("%s: owner=%s", privFrame.id, privFrame.owner));
      } else if (entry instanceof GeobFrame) {
        GeobFrame geobFrame = (GeobFrame) entry;
        Log.d(TAG, prefix + String.format("%s: mimeType=%s, filename=%s, description=%s",
            geobFrame.id, geobFrame.mimeType, geobFrame.filename, geobFrame.description));
      } else if (entry instanceof ApicFrame) {
        ApicFrame apicFrame = (ApicFrame) entry;
        Log.d(TAG, prefix + String.format(
            "%s: mimeType=%s, description=%s, pictureType = %s, pictureData = %s",
            apicFrame.id, apicFrame.mimeType, apicFrame.description, apicFrame.pictureType,
            Arrays.toString(apicFrame.pictureData)));

        pictureData = apicFrame.pictureData;

      } else if (entry instanceof CommentFrame) {
        CommentFrame commentFrame = (CommentFrame) entry;
        Log.d(TAG, prefix + String.format("%s: language=%s, description=%s", commentFrame.id,
            commentFrame.language, commentFrame.description));
      } else if (entry instanceof Id3Frame) {
        Id3Frame id3Frame = (Id3Frame) entry;
        Log.d(TAG, prefix + String.format("%s", id3Frame.id));
      } else if (entry instanceof EventMessage) {
        EventMessage eventMessage = (EventMessage) entry;
        Log.d(TAG, prefix + String.format("EMSG: scheme=%s, id=%d, value=%s",
            eventMessage.schemeIdUri, eventMessage.id, eventMessage.value));
      }
    }

    return new TrackMetadata(artistName, title, new ExoPlayerImage(pictureData));
  }
}
