package agency.tango.skald.spotify;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player.NotificationCallback;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.ArrayList;
import java.util.List;

import agency.tango.skald.core.listeners.LoginFailedListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.Player;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;

public class SkaldSpotifyPlayer implements Player {
  private static final String TAG = SkaldSpotifyPlayer.class.getSimpleName();
  private SpotifyPlayer spotifyPlayer;
  private final List<OnPlayerReadyListener> onPlayerReadyListeners = new ArrayList<>();
  private final List<LoginFailedListener> loginFailedListeners = new ArrayList<>();

  public SkaldSpotifyPlayer(Context context, String clientId, String oauthToken) {
    final Config playerConfig = new Config(context, oauthToken, clientId);

    spotifyPlayer = Spotify.getPlayer(playerConfig, this,
        new SpotifyPlayer.InitializationObserver() {
          @Override
          public void onInitialized(SpotifyPlayer spotifyPlayer) {
            spotifyPlayer.addNotificationCallback(new NotificationCallback() {
              @Override
              public void onPlaybackEvent(PlayerEvent playerEvent) {

              }

              @Override
              public void onPlaybackError(Error error) {

              }
            });

            spotifyPlayer.addConnectionStateCallback(new ConnectionStateCallback() {
              @Override
              public void onLoggedIn() {
                Log.d(TAG, "onLoggedIn");
                for(OnPlayerReadyListener onPlayerReadyListener : onPlayerReadyListeners) {
                  onPlayerReadyListener.onPlayerReady(SkaldSpotifyPlayer.this);
                }
              }

              @Override
              public void onLoggedOut() {

              }

              @Override
              public void onLoginFailed(Error error) {
                Log.e(TAG, String.format("onLoginFailed %s", error.toString()));
                for(LoginFailedListener loginFailedListener : loginFailedListeners) {
                  loginFailedListener.onLoginFailed();
                }
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
    Log.d(TAG, "Track played");

    Uri uri = track.getUri();
    String stringUri = uri.getPathSegments().get(uri.getPathSegments().size() - 1);
    spotifyPlayer.playUri(new SpotifyOperationCallback(), stringUri, 0, 0);
  }

  @Override
  public void play(SkaldPlaylist playlist) {
    Log.d(TAG, "Playlist played");

    Uri uri = playlist.getUri();
    String stringUri = uri.getPathSegments().get(uri.getPathSegments().size() - 1);
    spotifyPlayer.playUri(new SpotifyOperationCallback(), stringUri, 0, 0);
  }

  @Override
  public void stop() {

  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void release() {

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
  public void addLoginFailedListener(LoginFailedListener loginFailedListener) {
    loginFailedListeners.add(loginFailedListener);
  }

  @Override
  public void removeLoginFailedListener(LoginFailedListener loginFailedListener) {
    loginFailedListeners.remove(loginFailedListener);
  }
}
