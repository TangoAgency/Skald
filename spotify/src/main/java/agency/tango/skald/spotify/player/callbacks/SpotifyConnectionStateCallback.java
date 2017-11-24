package agency.tango.skald.spotify.player.callbacks;

import android.content.Context;
import android.util.Log;

import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;

import java.util.List;

import agency.tango.skald.core.listeners.OnErrorListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.spotify.api.models.Tokens;
import agency.tango.skald.spotify.authentication.SpotifyAuthData;
import agency.tango.skald.spotify.authentication.SpotifyAuthStore;
import agency.tango.skald.spotify.exceptions.SpotifyException;
import agency.tango.skald.spotify.exceptions.TokenRefreshException;
import agency.tango.skald.spotify.player.SkaldSpotifyPlayer;
import agency.tango.skald.spotify.provider.SpotifyProvider;
import agency.tango.skald.spotify.services.TokenService;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class SpotifyConnectionStateCallback implements ConnectionStateCallback {

  private static final String TAG = SpotifyConnectionStateCallback.class.getSimpleName();
  private final Context context;
  private final List<OnPlayerReadyListener> onPlayerReadyListeners;
  private final OnErrorListener onErrorListener;
  private final SkaldSpotifyPlayer skaldSpotifyPlayer;
  private final SpotifyProvider spotifyProvider;
  private final SpotifyAuthData spotifyAuthData;
  private final TokenService tokenService;
  private final SpotifyAuthStore spotifyAuthStore;

  public SpotifyConnectionStateCallback(Context context, SkaldSpotifyPlayer skaldSpotifyPlayer,
      List<OnPlayerReadyListener> onPlayerReadyListeners, OnErrorListener onErrorListener,
      SpotifyProvider spotifyProvider, SpotifyAuthData spotifyAuthData) {
    this.context = context;
    this.skaldSpotifyPlayer = skaldSpotifyPlayer;
    this.onPlayerReadyListeners = onPlayerReadyListeners;
    this.onErrorListener = onErrorListener;
    this.spotifyProvider = spotifyProvider;
    this.spotifyAuthData = spotifyAuthData;
    this.tokenService = new TokenService();
    this.spotifyAuthStore = new SpotifyAuthStore(spotifyProvider);
  }

  @Override
  public void onLoggedIn() {
    for (OnPlayerReadyListener onPlayerReadyListener : onPlayerReadyListeners) {
      onPlayerReadyListener.onPlayerReady(skaldSpotifyPlayer);
    }
  }

  @Override
  public void onLoggedOut() {

  }

  @Override
  public void onLoginFailed(Error error) {
    if (error == Error.kSpErrorLoginBadCredentials) {
      tokenService
          .getRefreshToken(spotifyProvider.getClientId(), spotifyProvider.getClientSecret(),
              spotifyAuthData.getRefreshToken())
          .subscribeOn(Schedulers.io())
          .subscribe(new DisposableSingleObserver<Tokens>() {
            @Override
            public void onSuccess(Tokens tokens) {
              skaldSpotifyPlayer.getPlayer().login(tokens.getAccessToken());
              saveTokens(context, tokens, spotifyAuthData);
            }

            @Override
            public void onError(Throwable error) {
              Log.e(TAG, "RefreshToken observer error", error);
              onErrorListener.onError(new TokenRefreshException("Could not refresh token", error));
            }
          });
    } else {
      onErrorListener.onError(new SpotifyException(error));
    }
  }

  @Override
  public void onTemporaryError() {
    onErrorListener.onError(new Exception("Temporary connection error in Spotify"));
  }

  @Override
  public void onConnectionMessage(String s) {

  }

  private void saveTokens(Context context, Tokens tokens, SpotifyAuthData spotifyAuthData) {
    SpotifyAuthData spotifyAuthDataRefreshed = new SpotifyAuthData(tokens.getAccessToken(),
        spotifyAuthData.getRefreshToken(), tokens.getExpiresIn());
    spotifyAuthStore.save(context, spotifyAuthDataRefreshed);
  }
}
