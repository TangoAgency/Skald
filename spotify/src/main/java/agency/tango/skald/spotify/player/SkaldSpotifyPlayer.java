package agency.tango.skald.spotify.player;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.Player.NotificationCallback;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.ArrayList;
import java.util.List;

import agency.tango.skald.core.Player;
import agency.tango.skald.core.errors.PlaybackError;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.core.models.TrackMetadata;
import agency.tango.skald.spotify.services.TokenService;
import agency.tango.skald.spotify.api.models.Tokens;
import agency.tango.skald.spotify.authentication.SpotifyAuthData;
import agency.tango.skald.spotify.authentication.SpotifyAuthStore;
import agency.tango.skald.spotify.player.callbacks.SpotifyOperationCallback;
import agency.tango.skald.spotify.provider.SpotifyProvider;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class SkaldSpotifyPlayer implements Player {
  private static final String TAG = SkaldSpotifyPlayer.class.getSimpleName();

  private final List<OnPlayerReadyListener> onPlayerReadyListeners = new ArrayList<>();
  private final List<OnPlaybackListener> onPlaybackListeners = new ArrayList<>();
  private final SpotifyOperationCallback spotifyOperationCallback = new SpotifyOperationCallback() {
    @Override
    public void onSuccess() {
      Log.i(TAG, "Operation succeed");
    }
  };
  private final ConnectionStateCallback connectionStateCallback;
  private final NotificationCallback notificationCallback;
  private final Context context;
  private final SpotifyProvider spotifyProvider;
  private final Handler mainHandler;

  private SpotifyPlayer spotifyPlayer;

  public SkaldSpotifyPlayer(final Context context, final SpotifyAuthData spotifyAuthData,
      final SpotifyProvider spotifyProvider) {
    this.context = context;
    this.spotifyProvider = spotifyProvider;
    mainHandler = new Handler(context.getMainLooper());
    connectionStateCallback = initializeConnectionStateCallback(spotifyAuthData);
    notificationCallback = initializeNotificationCallback();

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
  public void play(SkaldTrack track) {
    Uri uri = track.getUri();
    String stringUri = uri.getPathSegments().get(uri.getPathSegments().size() - 1);
    spotifyPlayer.playUri(spotifyOperationCallback, stringUri, 0, 0);
  }

  @Override
  public void play(SkaldPlaylist playlist) {
    Uri uri = playlist.getUri();
    String stringUri = uri.getPathSegments().get(uri.getPathSegments().size() - 1);
    spotifyPlayer.playUri(spotifyOperationCallback, stringUri, 0, 0);
  }

  @Override
  public void stop() {
    if (spotifyPlayer.getPlaybackState().isPlaying) {
      spotifyPlayer.pause(new SpotifyOperationCallback() {
        @Override
        public void onSuccess() {
          spotifyPlayer.seekToPosition(new SpotifyOperationCallback() {
            @Override
            public void onSuccess() {
              notifyStopEvent();
            }
          }, 0);
        }
      });
    }
  }

  @Override
  public void pause() {
    if (spotifyPlayer.getPlaybackState().isPlaying) {
      spotifyPlayer.pause(spotifyOperationCallback);
    }
  }

  @Override
  public void resume() {
    if (!spotifyPlayer.getPlaybackState().isPlaying) {
      spotifyPlayer.resume(spotifyOperationCallback);
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
  public boolean isPlaying() {
    return spotifyPlayer.getPlaybackState().isPlaying;
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

  private ConnectionStateCallback initializeConnectionStateCallback(
      final SpotifyAuthData spotifyAuthData) {
    return new ConnectionStateCallback() {
      @Override
      public void onLoggedIn() {
        for (OnPlayerReadyListener onPlayerReadyListener : onPlayerReadyListeners) {
          onPlayerReadyListener.onPlayerReady(SkaldSpotifyPlayer.this);
        }
      }

      @Override
      public void onLoggedOut() {

      }

      @Override
      public void onLoginFailed(Error error) {
        new TokenService()
            .getRefreshToken(spotifyProvider.getClientId(), spotifyProvider.getClientSecret(),
                spotifyAuthData.getRefreshToken())
            .subscribeOn(Schedulers.io())
            .subscribe(new DisposableSingleObserver<Tokens>() {
              @Override
              public void onSuccess(Tokens tokens) {
                spotifyPlayer.login(tokens.getAccessToken());
                saveTokens(context, tokens, spotifyAuthData);
              }

              @Override
              public void onError(Throwable error) {
                Log.e(TAG, "RefreshToken observer error", error);
              }
            });
      }

      @Override
      public void onTemporaryError() {

      }

      @Override
      public void onConnectionMessage(String s) {

      }
    };
  }

  private NotificationCallback initializeNotificationCallback() {
    return new NotificationCallback() {
      @Override
      public void onPlaybackEvent(PlayerEvent playerEvent) {
        Metadata metadata = spotifyPlayer.getMetadata();

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
        Log.e(TAG, String.format("PlaybackError occurred %s", error.toString()));
        for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
          onPlaybackListener.onError(new PlaybackError());
        }
      }
    };
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

  private void saveTokens(Context context, Tokens tokens, SpotifyAuthData spotifyAuthData) {
    SpotifyAuthData spotifyAuthDataRefreshed = new SpotifyAuthData(tokens.getAccessToken(),
        spotifyAuthData.getRefreshToken(), tokens.getExpiresIn());
    new SpotifyAuthStore(spotifyProvider).save(context, spotifyAuthDataRefreshed);
  }
}
