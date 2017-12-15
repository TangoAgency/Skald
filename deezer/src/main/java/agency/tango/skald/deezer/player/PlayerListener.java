package agency.tango.skald.deezer.player;

import android.os.Handler;

import com.deezer.sdk.model.PlayableEntity;
import com.deezer.sdk.model.Track;
import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.request.DeezerRequest;
import com.deezer.sdk.network.request.DeezerRequestFactory;
import com.deezer.sdk.network.request.event.JsonRequestListener;
import com.deezer.sdk.player.event.PlayerWrapperListener;

import java.util.List;

import agency.tango.skald.core.errors.PlaybackError;
import agency.tango.skald.core.listeners.OnErrorListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.models.ServiceImage;
import agency.tango.skald.core.models.TrackMetadata;
import agency.tango.skald.deezer.exceptions.UnparsedResultException;

public class PlayerListener implements PlayerWrapperListener {
  private static final String TRACK_REQUEST = "TRACK_REQUEST";
  private final DeezerPlayer deezerPLayer;
  private final DeezerConnect deezerConnect;
  private final List<OnPlaybackListener> onPlaybackListeners;
  private final OnErrorListener onErrorListener;
  private final Handler mainHandler;

  public PlayerListener(DeezerPlayer deezerPLayer, DeezerConnect deezerConnect,
      List<OnPlaybackListener> onPlaybackListeners, OnErrorListener onErrorListener,
      Handler mainHandler) {
    this.deezerPLayer = deezerPLayer;
    this.deezerConnect = deezerConnect;
    this.onPlaybackListeners = onPlaybackListeners;
    this.onErrorListener = onErrorListener;
    this.mainHandler = mainHandler;
  }

  @Override
  public void onAllTracksEnded() {

  }

  @Override
  public void onPlayTrack(PlayableEntity playableEntity) {
    makeTrackRequestAndNotifyPlayResumeEvent(playableEntity.getId());
  }

  @Override
  public void onTrackEnded(PlayableEntity playableEntity) {

  }

  @Override
  public void onRequestException(Exception exception, Object o) {
    onErrorListener.onError(exception);
  }

  private void makeTrackRequestAndNotifyPlayResumeEvent(long trackId) {
    DeezerRequest deezerRequest = DeezerRequestFactory.requestTrack(trackId);
    deezerRequest.setId(TRACK_REQUEST);
    deezerConnect.requestAsync(deezerRequest, new JsonRequestListener() {
      @Override
      public void onResult(Object result, Object requestId) {
        if (requestId.equals(TRACK_REQUEST)) {
          Track track = (Track) result;
          TrackMetadata trackMetadata = new TrackMetadata(track.getArtist().getName(),
              track.getTitle(), new ServiceImage(track.getAlbum().getImageUrl()));
          notifyPlayEvent(trackMetadata);
          deezerPLayer.notifyResumeEvent();
        }
      }

      @Override
      public void onUnparsedResult(String requestResponse, Object requestId) {
        for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
          onPlaybackListener.onError(
              new PlaybackError(new UnparsedResultException(
                  String.format("Cannot get track info: %s", requestResponse))));
        }
      }

      @Override
      public void onException(Exception exception, Object requestId) {
        for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
          onPlaybackListener.onError(new PlaybackError(exception));
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
}
