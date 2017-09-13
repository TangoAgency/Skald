package agency.tango.skald.spotify;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
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
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.core.models.TrackMetadata;
import agency.tango.skald.spotify.api.models.Tokens;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static agency.tango.skald.core.SkaldMusicService.EXTRA_AUTH_DATA;
import static agency.tango.skald.core.SkaldMusicService.INTENT_ACTION;

class SkaldSpotifyPlayer implements Player {
  private static final String TAG = SkaldSpotifyPlayer.class.getSimpleName();

  private final List<OnPlayerReadyListener> onPlayerReadyListeners = new ArrayList<>();
  private final List<OnPlaybackListener> onPlaybackListeners = new ArrayList<>();
  private final SpotifyOperationCallback spotifyOperationCallback = new SpotifyOperationCallback();
  private final Context context;
  private SpotifyPlayer spotifyPlayer;

  SkaldSpotifyPlayer(final Context context, final SpotifyAuthData spotifyAuthData,
      final String clientId, final String clientSecret) {
    this.context = context;
    final Config playerConfig = new Config(context, spotifyAuthData.getOauthToken(), clientId);

    spotifyPlayer = Spotify.getPlayer(playerConfig, this,
        new SpotifyPlayer.InitializationObserver() {
          @Override
          public void onInitialized(final SpotifyPlayer spotifyPlayer) {
            spotifyPlayer.addNotificationCallback(new NotificationCallback() {
              @Override
              public void onPlaybackEvent(PlayerEvent playerEvent) {
                Metadata metadata = spotifyPlayer.getMetadata();

                if (playerEvent == PlayerEvent.kSpPlaybackNotifyPlay) {
                  if (metadata.currentTrack != null) {
                    TrackMetadata trackMetadata = new TrackMetadata(
                        metadata.currentTrack.artistName,
                        metadata.currentTrack.name);
                    for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
                      onPlaybackListener.onPlayEvent(trackMetadata);
                    }
                  }
                } else if (playerEvent == PlayerEvent.kSpPlaybackNotifyPause) {
                  for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
                    onPlaybackListener.onPauseEvent();
                  }
                }
              }

              @Override
              public void onPlaybackError(Error error) {
                Log.e(TAG, String.format("PlaybackError occurred %s", error.toString()));
              }
            });

            spotifyPlayer.addConnectionStateCallback(new ConnectionStateCallback() {
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
                Log.e(TAG, String.format("onLoginFailed %s", error.toString()));

                new TokenService()
                    .getRefreshToken(clientId, clientSecret, spotifyAuthData.getRefreshToken())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new DisposableSingleObserver<Tokens>() {
                      @Override
                      public void onSuccess(Tokens tokens) {
                        spotifyPlayer.login(tokens.getAccessToken());

                        SpotifyAuthData spotifyAuthDataRefreshed = new SpotifyAuthData(
                            tokens.getAccessToken(), spotifyAuthData.getRefreshToken(),
                            tokens.getExpiresIn());
                        Intent intent = new Intent(INTENT_ACTION);
                        intent.putExtra(EXTRA_AUTH_DATA, spotifyAuthDataRefreshed);
                        LocalBroadcastManager
                            .getInstance(context)
                            .sendBroadcast(intent);
                      }

                      @Override
                      public void onError(Throwable error) {
                        Log.e(TAG, "RefreshToken Observer error", error);
                      }
                    });
              }

              @Override
              public void onTemporaryError() {

              }

              @Override
              public void onConnectionMessage(String s) {

              }
            });
          }

          @Override
          public void onError(Throwable throwable) {
            Log.e(TAG, "Could not initialize player", throwable);
          }
        });
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
      spotifyPlayer.pause(spotifyOperationCallback);
      spotifyPlayer.seekToPosition(spotifyOperationCallback, 0);
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
      for (OnPlaybackListener onPlaybackListener : onPlaybackListeners) {
        onPlaybackListener.onResumeEvent();
      }
    }
  }

  @Override
  public void release() {
    Spotify.destroyPlayer(context);
  }

  @Override
  public void addPlayerReadyListener(OnPlayerReadyListener onPlayerReadyListener) {
    onPlayerReadyListeners.add(onPlayerReadyListener);
  }

  @Override
  public void removePlayerReadyListener(OnPlayerReadyListener onPlayerReadyListener) {
    onPlayerReadyListeners.remove(onPlayerReadyListener);
  }

  @Override
  public void addOnPlaybackListener(OnPlaybackListener onPlaybackListener) {
    onPlaybackListeners.add(onPlaybackListener);
  }

  @Override
  public void removeOnPlaybackListener(OnPlaybackListener onPlaybackListener) {
    onPlaybackListeners.remove(onPlaybackListener);
  }
}
