package agency.tango.skald.spotify.player.callbacks;

import android.os.Handler;

import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;

import java.util.List;

import agency.tango.skald.core.errors.PlaybackError;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.models.TrackMetadata;
import agency.tango.skald.spotify.exceptions.SpotifyError;
import agency.tango.skald.spotify.player.SkaldSpotifyPlayer;

public class SpotifyNotificationCallback implements Player.NotificationCallback {
  private final SkaldSpotifyPlayer skaldSpotifyPlayer;
  private final List<OnPlaybackListener> onPlaybackListeners;
  private final Handler mainHandler;

  public SpotifyNotificationCallback(SkaldSpotifyPlayer skaldSpotifyPlayer, Handler mainHandler,
      List<OnPlaybackListener> onPlaybackListeners) {
    this.skaldSpotifyPlayer = skaldSpotifyPlayer;
    this.onPlaybackListeners = onPlaybackListeners;
    this.mainHandler = mainHandler;
  }

  @Override
  public void onPlaybackEvent(PlayerEvent playerEvent) {
    Metadata metadata = skaldSpotifyPlayer.getPlayer().getMetadata();

    if (playerEvent == PlayerEvent.kSpPlaybackNotifyTrackChanged) {
      if (metadata.currentTrack != null) {
        notifyPlayEvent(metadata);
      }
    } else if (playerEvent == PlayerEvent.kSpPlaybackNotifyPlay) {
      if (metadata.currentTrack != null) {
        notifyResumeEvent();
      }
    } else if (playerEvent == PlayerEvent.kSpPlaybackNotifyPause) {
      notifyPauseEvent();
    }
  }

  @Override
  public void onPlaybackError(Error error) {
    for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
      onPlaybackListener.onError(new PlaybackError(new SpotifyError(error)));
    }
  }

  private void notifyPlayEvent(final Metadata metadata) {
    mainHandler.post(new Runnable() {
      @Override
      public void run() {
        TrackMetadata trackMetadata = new TrackMetadata(metadata.currentTrack.artistName,
            metadata.currentTrack.name, metadata.currentTrack.albumCoverWebUrl);
        for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
          onPlaybackListener.onPlayEvent(trackMetadata);
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
}
