package agency.tango.skald.deezer.player;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.player.PlayerWrapper;
import com.deezer.sdk.player.PlaylistPlayer;
import com.deezer.sdk.player.TrackPlayer;
import com.deezer.sdk.player.event.OnBufferErrorListener;
import com.deezer.sdk.player.event.OnPlayerErrorListener;
import com.deezer.sdk.player.event.OnPlayerStateChangeListener;
import com.deezer.sdk.player.event.PlayerState;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;
import com.deezer.sdk.player.networkcheck.WifiAndMobileNetworkStateChecker;
import java.util.ArrayList;
import java.util.List;
import agency.tango.skald.core.cache.TLruCache;
import agency.tango.skald.core.callbacks.SkaldOperationCallback;
import agency.tango.skald.core.errors.PlaybackError;
import agency.tango.skald.core.listeners.OnErrorListener;
import agency.tango.skald.core.listeners.OnLoadingListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;

class DeezerPlayer {
  private static final int MAX_NUMBER_OF_PLAYERS = 2;
  private final Context context;
  private final DeezerConnect deezerConnect;
  private final List<OnPlaybackListener> onPlaybackListeners = new ArrayList<>();
  private final List<OnLoadingListener> onLoadingListeners = new ArrayList<>();
  private final OnErrorListener onErrorListener;
  private final TLruCache<Class, PlayerWrapper> playerCache = new TLruCache<>(MAX_NUMBER_OF_PLAYERS,
      (key, playerWrapper) -> {
        PlayerState playerState = playerWrapper.getPlayerState();
        if (playerState == PlayerState.PLAYING) {
          playerWrapper.stop();
        }
        if (playerState != PlayerState.RELEASED) {
          playerWrapper.release();
        }
      });
  private final Handler mainHandler;

  private PlayerWrapper currentPlayer;
  private boolean isPlayEvent = false;
  private SkaldOperationCallback currentSkaldOperationCallback;

  DeezerPlayer(Context context, DeezerConnect deezerConnect, OnErrorListener onErrorListener) {
    this.context = context;
    this.deezerConnect = deezerConnect;
    this.onErrorListener = onErrorListener;
    mainHandler = new Handler(context.getMainLooper());
  }

  void play(SkaldPlayableEntity skaldPlayableEntity, SkaldOperationCallback operationCallback) {
    currentSkaldOperationCallback = operationCallback;
    if (currentPlayer != null && isPlaying()) {
      currentPlayer.stop();
    }
    if (skaldPlayableEntity instanceof SkaldTrack) {
      play((SkaldTrack) skaldPlayableEntity, operationCallback);
    } else if (skaldPlayableEntity instanceof SkaldPlaylist) {
      play((SkaldPlaylist) skaldPlayableEntity, operationCallback);
    }
  }

  void stop(SkaldOperationCallback skaldOperationCallback) {
    if (isPlaying()) {
      currentSkaldOperationCallback = skaldOperationCallback;
      currentPlayer.stop();
    }
  }

  void pause(SkaldOperationCallback skaldOperationCallback) {
    if (isPlaying()) {
      currentSkaldOperationCallback = skaldOperationCallback;
      currentPlayer.pause();
    }
  }

  void resume(SkaldOperationCallback skaldOperationCallback) {
    if (!isPlaying()) {
      currentSkaldOperationCallback = skaldOperationCallback;
      currentPlayer.play();
    }
  }

  void release() {
    playerCache.evictAll();
  }

  void addOnPlayerReadyListener(OnPlaybackListener onPlaybackListener) {
    onPlaybackListeners.add(onPlaybackListener);
  }

  void removeOnPlayerReadyListener(OnPlaybackListener onPlaybackListener) {
    onPlaybackListeners.remove(onPlaybackListener);
  }

  void addOnLoadingListener(OnLoadingListener onLoadingListener) {
    onLoadingListeners.add(onLoadingListener);
  }

  void removeOnLoadingListener(OnLoadingListener onLoadingListener) {
    onLoadingListeners.remove(onLoadingListener);
  }

  void notifyResumeEvent() {
    mainHandler.post(() -> {
      for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
        onPlaybackListener.onResumeEvent();
      }
    });
  }

  boolean isPlaying() {
    return currentPlayer.getPlayerState() == PlayerState.PLAYING;
  }

  private void play(SkaldTrack skaldTrack, SkaldOperationCallback operationCallback) {
    notifyLoadingEvent();
    long trackId = getId(skaldTrack.getUri());
    TrackPlayer trackPlayer = getPlayer(TrackPlayer.class);
    if (trackPlayer == null) {
      try {
        trackPlayer = new TrackPlayer((Application) context.getApplicationContext(),
            deezerConnect, new WifiAndMobileNetworkStateChecker());
        trackPlayer.addPlayerListener(new PlayerListener(this, deezerConnect,
            onPlaybackListeners, onErrorListener, mainHandler));
        playerCache.put(TrackPlayer.class, trackPlayer);
        currentPlayer = trackPlayer;
        addOnPlayerStateChangeListener();
        addPlayerErrorListeners();
        playTrack(trackId, trackPlayer);
      } catch (TooManyPlayersExceptions tooManyPlayersExceptions) {
        handleTooManyPlayerException(tooManyPlayersExceptions, operationCallback);
        play(skaldTrack, operationCallback);
      } catch (DeezerError deezerError) {
        handleDeezerError(deezerError, operationCallback);
      }
    } else {
      currentPlayer = trackPlayer;
      playTrack(trackId, trackPlayer);
    }
  }

  private void play(SkaldPlaylist skaldPlaylist, SkaldOperationCallback operationCallback) {
    notifyLoadingEvent();
    long playlistId = getId(skaldPlaylist.getUri());
    PlaylistPlayer playlistPlayer = getPlayer(PlaylistPlayer.class);
    if (playlistPlayer == null) {
      try {
        playlistPlayer = new PlaylistPlayer(
            (Application) context.getApplicationContext(), deezerConnect,
            new WifiAndMobileNetworkStateChecker());
        playlistPlayer.addPlayerListener(new PlayerListener(this, deezerConnect,
            onPlaybackListeners, onErrorListener, mainHandler));
        playerCache.put(PlaylistPlayer.class, playlistPlayer);
        currentPlayer = playlistPlayer;
        addOnPlayerStateChangeListener();
        addPlayerErrorListeners();
        playPlaylist(playlistId, playlistPlayer);
      } catch (TooManyPlayersExceptions tooManyPlayersExceptions) {
        handleTooManyPlayerException(tooManyPlayersExceptions, operationCallback);
        play(skaldPlaylist, operationCallback);
      } catch (DeezerError deezerError) {
        handleDeezerError(deezerError, operationCallback);
      }
    } else {
      currentPlayer = playlistPlayer;
      playPlaylist(playlistId, playlistPlayer);
    }
  }

  private void playTrack(long trackId, TrackPlayer trackPlayer) {
    trackPlayer.playTrack(trackId);
  }

  private void playPlaylist(long playlistId, PlaylistPlayer playlistPlayer) {
    playlistPlayer.playPlaylist(playlistId);
  }

  private void notifyLoadingEvent() {
    mainHandler.post(() -> {
      for (OnLoadingListener onLoadingListener : onLoadingListeners) {
        onLoadingListener.onLoading();
      }
    });
  }

  private void addOnPlayerStateChangeListener() {
    currentPlayer.addOnPlayerStateChangeListener((playerState, timePosition) -> {
      if (playerState == PlayerState.PLAYING) {
        if (!isPlayEvent) {
          notifyResumeEvent();
        }
        isPlayEvent = false;
        currentSkaldOperationCallback.onSuccess();
      } else if (playerState == PlayerState.WAITING_FOR_DATA) {
        isPlayEvent = true;
      } else if (playerState == PlayerState.PAUSED) {
        notifyPauseEvent();
        currentSkaldOperationCallback.onSuccess();
      } else if (playerState == PlayerState.STOPPED) {
        notifyPauseEvent();
        notifyStopEvent();
        currentSkaldOperationCallback.onSuccess();
      }
    });
  }

  private void addPlayerErrorListeners() {
    currentPlayer.addOnPlayerErrorListener(
        (exception, timePosition) -> onErrorListener.onError(exception));

    currentPlayer.addOnBufferErrorListener(
        (exception, percent) -> onErrorListener.onError(exception));
  }

  private void notifyPauseEvent() {
    mainHandler.post(() -> {
      for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
        onPlaybackListener.onPauseEvent();
      }
    });
  }

  private void notifyStopEvent() {
    mainHandler.post(() -> {
      for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
        onPlaybackListener.onStopEvent();
      }
    });
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

  private void handleTooManyPlayerException(TooManyPlayersExceptions tooManyPlayersExceptions,
      SkaldOperationCallback operationCallback) {
    tooManyPlayersExceptions.getMessage();
    tooManyPlayersExceptions.printStackTrace();
    playerCache.evictAll();
    operationCallback.onError(tooManyPlayersExceptions);
  }

  private void handleDeezerError(DeezerError deezerError,
      SkaldOperationCallback operationCallback) {
    deezerError.getMessage();
    deezerError.printStackTrace();
    notifyPlaybackError(deezerError);
    operationCallback.onError(deezerError);
  }

  private void notifyPlaybackError(Exception exception) {
    for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
      onPlaybackListener.onError(new PlaybackError(exception));
    }
  }

  private long getId(Uri uri) {
    String stringUri = uri.getPathSegments().get(uri.getPathSegments().size() - 1);
    return Long.parseLong(stringUri);
  }
}
