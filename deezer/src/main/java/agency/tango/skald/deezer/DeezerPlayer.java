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

import java.util.ArrayList;
import java.util.List;

import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;

class DeezerPlayer {
  private final Context context;
  private final DeezerConnect deezerConnect;

  private final List<PlayerWrapper> players = new ArrayList<>();
  private PlayerWrapper actualPlayer;

  DeezerPlayer(Context context, DeezerConnect deezerConnect) {
    this.context = context;
    this.deezerConnect = deezerConnect;
  }

  void play(SkaldTrack skaldTrack) {
    if (!isTrackPlayerInitialized()) {
      try {
        TrackPlayer trackPlayer = new TrackPlayer((Application) context.getApplicationContext(),
            deezerConnect, new WifiAndMobileNetworkStateChecker());
        players.add(trackPlayer);
        actualPlayer = trackPlayer;
      } catch (TooManyPlayersExceptions tooManyPlayersExceptions) {
        releaseNotUsedPlayers();
        play(skaldTrack);
        tooManyPlayersExceptions.printStackTrace();
      } catch (DeezerError deezerError) {
        deezerError.printStackTrace();
      }
    }
    long trackId = getId(skaldTrack.getUri());
    ((TrackPlayer) actualPlayer).playTrack(trackId);
  }

  private boolean isTrackPlayerInitialized() {
    for (PlayerWrapper playerWrapper : players) {
      if (playerWrapper instanceof TrackPlayer) {
        actualPlayer = playerWrapper;
        return true;
      }
    }
    return false;
  }

  void play(SkaldPlaylist skaldPlaylist) {
    if (!isPlaylistPlayerInitialized()) {
      try {
        PlaylistPlayer playlistPlayer = new PlaylistPlayer(
            (Application) context.getApplicationContext(), deezerConnect,
            new WifiAndMobileNetworkStateChecker());
        players.add(playlistPlayer);
        actualPlayer = playlistPlayer;
      } catch (TooManyPlayersExceptions tooManyPlayersExceptions) {
        releaseNotUsedPlayers();
        play(skaldPlaylist);
        tooManyPlayersExceptions.printStackTrace();
      } catch (DeezerError deezerError) {
        deezerError.printStackTrace();
      }
    }
    long playlistId = getId(skaldPlaylist.getUri());
    ((PlaylistPlayer) actualPlayer).playPlaylist(playlistId);
  }

  private boolean isPlaylistPlayerInitialized() {
    for (PlayerWrapper playerWrapper : players) {
      if (playerWrapper instanceof PlaylistPlayer) {
        actualPlayer = playerWrapper;
        return true;
      }
    }
    return false;
  }

  private void releaseNotUsedPlayers() {
    String className = actualPlayer.getClass().getSimpleName();
    for (PlayerWrapper playerWrapper : players) {
      if (!playerWrapper.getClass().getSimpleName().equals(className)) {
        releasePlayer(playerWrapper);
      }
    }
  }

  private long getId(Uri uri) {
    String stringUri = uri.getPathSegments().get(uri.getPathSegments().size() - 1);
    return Long.parseLong(stringUri);
  }

  void stop() {
    if(actualPlayer.getPlayerState() == PlayerState.PLAYING) {
      actualPlayer.stop();
    }
  }

  void pause() {
    if(actualPlayer.getPlayerState() == PlayerState.PLAYING) {
      actualPlayer.pause();
    }
  }

  void resume() {
    if(actualPlayer.getPlayerState() != PlayerState.PLAYING) {
      actualPlayer.play();
    }
  }

  void release() {
    for (PlayerWrapper playerWrapper : players) {
      releasePlayer(playerWrapper);
    }
  }

  private void releasePlayer(PlayerWrapper playerWrapper) {
    if (playerWrapper != null) {
      PlayerState playerState = playerWrapper.getPlayerState();
      if (playerState == PlayerState.PLAYING) {
        playerWrapper.stop();
      }
      if (playerState != PlayerState.RELEASED) {
        playerWrapper.release();
      }
    }
  }
}
