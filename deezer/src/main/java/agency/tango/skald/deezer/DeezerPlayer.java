package agency.tango.skald.deezer;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.player.PlayerWrapper;
import com.deezer.sdk.player.PlaylistPlayer;
import com.deezer.sdk.player.TrackPlayer;
import com.deezer.sdk.player.event.PlayerState;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;
import com.deezer.sdk.player.networkcheck.WifiAndMobileNetworkStateChecker;

import agency.tango.skald.core.LruCache;
import agency.tango.skald.core.TLruCache;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;

class DeezerPlayer {
  private static final int MAX_NUMBER_OF_PLAYERS = 2;
  private final Context context;
  private final DeezerConnect deezerConnect;

  private TLruCache<Class, PlayerWrapper> playerCache;
  private PlayerWrapper currentPlayer;

  DeezerPlayer(Context context, DeezerConnect deezerConnect) {
    this.context = context;
    this.deezerConnect = deezerConnect;
    playerCache = new TLruCache<>(MAX_NUMBER_OF_PLAYERS,
        new LruCache.CacheItemRemovedListener<Class, PlayerWrapper>() {
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
  }

  void play(SkaldTrack skaldTrack) throws DeezerError {
    long trackId = getId(skaldTrack.getUri());
    TrackPlayer trackPlayer = getPlayer(TrackPlayer.class);
    if (trackPlayer == null) {
      try {
        trackPlayer = new TrackPlayer((Application) context.getApplicationContext(),
            deezerConnect, new WifiAndMobileNetworkStateChecker());
        playerCache.put(TrackPlayer.class, trackPlayer);
        currentPlayer = trackPlayer;
        trackPlayer.playTrack(trackId);
      } catch (TooManyPlayersExceptions tooManyPlayersExceptions) {
        handleTooManyPlayerException(tooManyPlayersExceptions);
        play(skaldTrack);
      }
    } else {
      trackPlayer.playTrack(trackId);
    }
  }

  void play(SkaldPlaylist skaldPlaylist) throws DeezerError {
    long playlistId = getId(skaldPlaylist.getUri());
    PlaylistPlayer playlistPlayer = getPlayer(PlaylistPlayer.class);
    if (playlistPlayer == null) {
      try {
        playlistPlayer = new PlaylistPlayer(
            (Application) context.getApplicationContext(), deezerConnect,
            new WifiAndMobileNetworkStateChecker());
        playerCache.put(PlaylistPlayer.class, playlistPlayer);
        currentPlayer = playlistPlayer;
        playlistPlayer.playPlaylist(playlistId);
      } catch (TooManyPlayersExceptions tooManyPlayersExceptions) {
        handleTooManyPlayerException(tooManyPlayersExceptions);
        play(skaldPlaylist);
      }
    } else {
      playlistPlayer.playPlaylist(playlistId);
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

  boolean isPlaying() {
    return currentPlayer.getPlayerState() == PlayerState.PLAYING;
  }

  private <T> T getPlayer(Class<T> a) {
    for (PlayerWrapper playerWrapper : playerCache.snapshot().values()) {
      if (a.isAssignableFrom(playerWrapper.getClass())) {
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
