package agency.tango.skald.spotify.player;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player.NotificationCallback;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.ArrayList;
import java.util.List;

import agency.tango.skald.core.Player;
import agency.tango.skald.core.callbacks.SkaldOperationCallback;
import agency.tango.skald.core.listeners.OnLoadingListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.spotify.authentication.SpotifyAuthData;
import agency.tango.skald.spotify.player.callbacks.SpotifyConnectionStateCallback;
import agency.tango.skald.spotify.player.callbacks.SpotifyNotificationCallback;
import agency.tango.skald.spotify.player.callbacks.SpotifyOperationCallback;
import agency.tango.skald.spotify.provider.SpotifyProvider;

public class SkaldSpotifyPlayer implements Player {
  private static final String TAG = SkaldSpotifyPlayer.class.getSimpleName();

  private final List<OnPlayerReadyListener> onPlayerReadyListeners = new ArrayList<>();
  private final List<OnLoadingListener> onLoadingListeners = new ArrayList<>();
  private final List<OnPlaybackListener> onPlaybackListeners = new ArrayList<>();
  private final ConnectionStateCallback connectionStateCallback;
  private final NotificationCallback notificationCallback;
  private final Context context;
  private final Handler mainHandler;

  private SpotifyPlayer spotifyPlayer;

  public SkaldSpotifyPlayer(final Context context, final SpotifyAuthData spotifyAuthData,
      final SpotifyProvider spotifyProvider) {
    this.context = context;
    mainHandler = new Handler(context.getMainLooper());
    connectionStateCallback = new SpotifyConnectionStateCallback(context, this,
        onPlayerReadyListeners, spotifyProvider, spotifyAuthData);
    notificationCallback = new SpotifyNotificationCallback(this, mainHandler, onPlaybackListeners);

    final Config playerConfig = new Config(context, spotifyAuthData.getOauthToken(),
        spotifyProvider.getClientId());

    spotifyPlayer = Spotify.getPlayer(playerConfig, this,
        new SpotifyPlayer.InitializationObserver() {
          @Override
          public void onInitialized(final SpotifyPlayer spotifyPlayer) {
            spotifyPlayer.addConnectionStateCallback(connectionStateCallback);
            spotifyPlayer.addNotificationCallback(notificationCallback);
          }

          @Override
          public void onError(Throwable throwable) {
            Log.e(TAG, "Could not initialize player", throwable);
          }
        });

    spotifyPlayer.login(spotifyAuthData.getOauthToken());
  }

  @Override
  public void play(SkaldPlayableEntity playableEntity, SkaldOperationCallback operationCallback) {
    Log.d("test", "SpotifyPlayStart");
    notifyLoadingEvent();
    spotifyPlayer.playUri(new SpotifyOperationCallback(operationCallback),
        getUriToPlay(playableEntity.getUri()), 0, 0);
  }

  @Override
  public void stop(final SkaldOperationCallback skaldOperationCallback) {
    if (spotifyPlayer.getPlaybackState().isPlaying) {
      Log.d("test", "SpotifyStopStart");
      spotifyPlayer.pause(new SpotifyOperationCallback() {
        @Override
        public void onSuccess() {
          spotifyPlayer.seekToPosition(new SpotifyOperationCallback() {
            @Override
            public void onSuccess() {
              notifyStopEvent();
              Log.d("test", "Stop succeed in Spotify");
              skaldOperationCallback.onSuccess();
            }
          }, 0);
        }
      });
    }
  }

  @Override
  public void pause(SkaldOperationCallback skaldOperationCallback) {
    if (spotifyPlayer.getPlaybackState().isPlaying) {
      spotifyPlayer.pause(new SpotifyOperationCallback(skaldOperationCallback));
    }
  }

  @Override
  public void resume(SkaldOperationCallback skaldOperationCallback) {
    if (!spotifyPlayer.getPlaybackState().isPlaying) {
      spotifyPlayer.resume(new SpotifyOperationCallback(skaldOperationCallback));
    }
  }

  @Override
  public void release() {
    spotifyPlayer.logout();
    spotifyPlayer.removeNotificationCallback(notificationCallback);
    spotifyPlayer.removeConnectionStateCallback(connectionStateCallback);
    Spotify.destroyPlayer(context);
  }

  @Override
  public void addOnPlayerReadyListener(OnPlayerReadyListener onPlayerReadyListener) {
    onPlayerReadyListeners.add(onPlayerReadyListener);
  }

  @Override
  public void removeOnPlayerReadyListener() {
    onPlayerReadyListeners.remove(0);
  }

  @Override
  public void addOnPlaybackListener(OnPlaybackListener onPlaybackListener) {
    onPlaybackListeners.add(onPlaybackListener);
  }

  @Override
  public void removeOnPlaybackListener() {
    onPlaybackListeners.remove(0);
  }

  @Override
  public void addOnLoadingListener(OnLoadingListener onLoadingListener) {
    onLoadingListeners.add(onLoadingListener);
  }

  @Override
  public void removeOnLoadingListener() {
    onLoadingListeners.remove(0);
  }

  public SpotifyPlayer getPlayer() {
    return spotifyPlayer;
  }

  private String getUriToPlay(Uri uri) {
    return uri.getPathSegments().get(uri.getPathSegments().size() - 1);
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
}
