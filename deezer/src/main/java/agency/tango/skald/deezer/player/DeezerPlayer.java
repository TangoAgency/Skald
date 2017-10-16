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

class DeezerPlayer {
  private static final int MAX_NUMBER_OF_PLAYERS = 2;
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
  private boolean isPlayEvent = false;

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

  void notifyResumeEvent() {
    mainHandler.post(new Runnable() {
      @Override
      public void run() {
        for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
          onPlaybackListener.onResumeEvent();
        }
      }
    });
  }

  private void play(SkaldTrack skaldTrack) {
    long trackId = getId(skaldTrack.getUri());
    TrackPlayer trackPlayer = getPlayer(TrackPlayer.class);
    if (trackPlayer == null) {
      try {
        trackPlayer = new TrackPlayer((Application) context.getApplicationContext(),
            deezerConnect, new WifiAndMobileNetworkStateChecker());
        trackPlayer.addPlayerListener(new PlayerListener(this, deezerConnect, onPlaybackListeners,
            mainHandler));
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
        playlistPlayer.addPlayerListener(new PlayerListener(this, deezerConnect,
            onPlaybackListeners, mainHandler));
        playerCache.put(PlaylistPlayer.class, playlistPlayer);
        currentPlayer = playlistPlayer;
        addOnPlayerStateChangeListener();
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
    for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
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
          if (!isPlayEvent) {
            notifyResumeEvent();
          }
          isPlayEvent = false;
        } else if (playerState == PlayerState.WAITING_FOR_DATA) {
          isPlayEvent = true;
        } else if (playerState == PlayerState.PAUSED) {
          notifyPauseEvent();
        } else if (playerState == PlayerState.STOPPED) {
          notifyPauseEvent();
          notifyStopEvent();
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
