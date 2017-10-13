package agency.tango.skald.deezer.player;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import com.deezer.sdk.model.Track;
import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.request.DeezerRequest;
import com.deezer.sdk.network.request.DeezerRequestFactory;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.network.request.event.JsonRequestListener;
import com.deezer.sdk.player.PlayerWrapper;
import com.deezer.sdk.player.PlaylistPlayer;
import com.deezer.sdk.player.TrackPlayer;
import com.deezer.sdk.player.event.OnPlayerStateChangeListener;
import com.deezer.sdk.player.event.PlayerState;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;
import com.deezer.sdk.player.networkcheck.WifiAndMobileNetworkStateChecker;

import java.util.ArrayList;
import java.util.List;

import agency.tango.skald.core.cache.SkaldLruCache;
import agency.tango.skald.core.cache.TLruCache;
import agency.tango.skald.core.errors.PlaybackError;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.core.models.TrackMetadata;

class DeezerPlayer {
  private static final int MAX_NUMBER_OF_PLAYERS = 2;
  private static final String TRACK_REQUEST = "TRACK_REQUEST";
  private final Context context;
  private final DeezerConnect deezerConnect;
  private final List<OnPlaybackListener> onPlaybackListeners = new ArrayList<>();
  private final TLruCache<Class, PlayerWrapper> playerCache = new TLruCache<>(MAX_NUMBER_OF_PLAYERS,
      new SkaldLruCache.CacheItemRemovedListener<Class, PlayerWrapper>() {
        @Override
        public void release(Class key, PlayerWrapper playerWrapper) {
          PlayerState playerState = playerWrapper.getPlayerState();
          if (playerState == PlayerState.PLAYING) {
            playerWrapper.stop();
          }
          if (playerState != PlayerState.RELEASED) {
            playerWrapper.release();
          }
        }
      });
  private final Handler mainHandler;

  private PlayerWrapper currentPlayer;
  private SkaldTrack skaldTrack;
  private boolean isTrackBeingPlaying = false;

  DeezerPlayer(Context context, DeezerConnect deezerConnect) {
    this.context = context;
    this.deezerConnect = deezerConnect;
    mainHandler = new Handler(context.getMainLooper());
  }

  void play(SkaldPlayableEntity skaldPlayableEntity) {
    if (skaldPlayableEntity instanceof SkaldTrack) {
      play((SkaldTrack) skaldPlayableEntity);
    } else if (skaldPlayableEntity instanceof SkaldPlaylist) {
      play((SkaldPlaylist) skaldPlayableEntity);
    }
  }

  void stop() {
    if (isPlaying()) {
      currentPlayer.stop();
    }
  }

  void pause() {
    if (isPlaying()) {
      currentPlayer.pause();
    }
  }

  void resume() {
    if (!isPlaying()) {
      currentPlayer.play();
    }
  }

  void release() {
    playerCache.evictAll();
  }

  void addOnPlayerReadyListener(OnPlaybackListener onPlaybackListener) {
    onPlaybackListeners.add(onPlaybackListener);
  }

  void removeOnPlayerReadyListener() {
    onPlaybackListeners.remove(0);
  }

  private void play(SkaldTrack skaldTrack) {
    this.skaldTrack = skaldTrack;
    long trackId = getId(skaldTrack.getUri());
    TrackPlayer trackPlayer = getPlayer(TrackPlayer.class);
    if (trackPlayer == null) {
      try {
        trackPlayer = new TrackPlayer((Application) context.getApplicationContext(),
            deezerConnect, new WifiAndMobileNetworkStateChecker());
        playerCache.put(TrackPlayer.class, trackPlayer);
        currentPlayer = trackPlayer;
        addOnPlayerStateChangeListener();
        trackPlayer.playTrack(trackId);
      } catch (TooManyPlayersExceptions tooManyPlayersExceptions) {
        handleTooManyPlayerException(tooManyPlayersExceptions);
        play(skaldTrack);
      } catch (DeezerError deezerError) {
        deezerError.printStackTrace();
        notifyPlaybackError(deezerError.getMessage());
      }
    } else {
      currentPlayer = trackPlayer;
      trackPlayer.playTrack(trackId);
    }
  }

  private void play(SkaldPlaylist skaldPlaylist) {
    long playlistId = getId(skaldPlaylist.getUri());
    PlaylistPlayer playlistPlayer = getPlayer(PlaylistPlayer.class);
    if (playlistPlayer == null) {
      try {
        playlistPlayer = new PlaylistPlayer(
            (Application) context.getApplicationContext(), deezerConnect,
            new WifiAndMobileNetworkStateChecker());
        playerCache.put(PlaylistPlayer.class, playlistPlayer);
        currentPlayer = playlistPlayer;
        //addOnPlayerStateChangeListener();
        playlistPlayer.playPlaylist(playlistId);
      } catch (TooManyPlayersExceptions tooManyPlayersExceptions) {
        handleTooManyPlayerException(tooManyPlayersExceptions);
        play(skaldPlaylist);
      } catch (DeezerError deezerError) {
        deezerError.printStackTrace();
        notifyPlaybackError(deezerError.getMessage());
      }
    } else {
      currentPlayer = playlistPlayer;
      playlistPlayer.playPlaylist(playlistId);
    }
  }

  private void notifyPlaybackError(String message) {
    for(OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
      onPlaybackListener.onError(new PlaybackError(message));
    }
  }

  private boolean isPlaying() {
    return currentPlayer.getPlayerState() == PlayerState.PLAYING;
  }

  private void addOnPlayerStateChangeListener() {
    currentPlayer.addOnPlayerStateChangeListener(new OnPlayerStateChangeListener() {
      @Override
      public void onPlayerStateChange(PlayerState playerState, long timePosition) {
        if (playerState == PlayerState.PLAYING) {
          if (isTrackBeingPlaying) {
            notifyResumeEvent();
          } else {
            makeTrackRequestAndNotifyPlayResumeEvent(skaldTrack);
          }
        } else if (playerState == PlayerState.PAUSED) {
          notifyPauseEvent();
        } else if (playerState == PlayerState.STOPPED) {
          notifyPauseEvent();
          notifyStopEvent();
        }
      }
    });
  }

  private void makeTrackRequestAndNotifyPlayResumeEvent(final SkaldTrack skaldTrack) {
    DeezerRequest deezerRequest = DeezerRequestFactory.requestTrack(getId(skaldTrack.getUri()));
    deezerRequest.setId(TRACK_REQUEST);
    deezerConnect.requestAsync(deezerRequest, new JsonRequestListener() {
      @Override
      public void onResult(Object result, Object requestId) {
        if (requestId.equals(TRACK_REQUEST)) {
          Track track = (Track) result;
          TrackMetadata trackMetadata = new TrackMetadata(track.getArtist().getName(),
              track.getTitle(), track.getAlbum().getImageUrl());
          notifyPlayEvent(trackMetadata);
          notifyResumeEvent();
        }
      }

      @Override
      public void onUnparsedResult(String requestResponse, Object requestId) {
        for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
          onPlaybackListener.onError(new PlaybackError("Cannot get track info"));
        }
      }

      @Override
      public void onException(Exception exception, Object requestId) {
        for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
          onPlaybackListener.onError(new PlaybackError(exception.getMessage()));
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
    isTrackBeingPlaying = true;
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

  private void notifyStopEvent() {
    mainHandler.post(new Runnable() {
      @Override
      public void run() {
        for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
          onPlaybackListener.onStopEvent();
        }
      }
    });
    isTrackBeingPlaying = false;
  }

  private <T> T getPlayer(Class<T> type) {
    for (PlayerWrapper playerWrapper : playerCache.snapshot().values()) {
      if (type.isAssignableFrom(playerWrapper.getClass())) {
        currentPlayer = playerWrapper;
        return (T) playerWrapper;
      }
    }
    return null;
  }

  private void handleTooManyPlayerException(TooManyPlayersExceptions tooManyPlayersExceptions) {
    tooManyPlayersExceptions.getMessage();
    tooManyPlayersExceptions.printStackTrace();
    playerCache.evictAll();
  }

  private long getId(Uri uri) {
    String stringUri = uri.getPathSegments().get(uri.getPathSegments().size() - 1);
    return Long.parseLong(stringUri);
  }
}
