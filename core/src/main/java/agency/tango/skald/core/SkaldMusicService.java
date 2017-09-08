package agency.tango.skald.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import agency.tango.skald.core.listeners.AuthErrorListener;
import agency.tango.skald.core.listeners.LoginFailedListener;
import agency.tango.skald.core.listeners.OnErrorListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.listeners.OnPreparedListener;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import io.reactivex.Observable;

public class SkaldMusicService {
  public static final String INTENT_ACTION = "spotify_auth_action";
  private static final String TAG = SkaldMusicService.class.getSimpleName();

  private final List<OnPreparedListener> onPreparedListeners = new ArrayList<>();
  private final List<OnErrorListener> onErrorListeners = new ArrayList<>();
  private final List<AuthErrorListener> authErrorListeners = new ArrayList<>();
  private final List<Provider> providers = new ArrayList<>();
  private final Context context;

  private Player player;
  private SkaldTrack currentTrack;
  private SkaldPlaylist currentPlaylist;
  private SkaldAuthData skaldAuthData;

  private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      skaldAuthData = intent.getExtras().getParcelable("authData");
      getSkaldAuthStore().save(skaldAuthData, context);
      player = getPlayer();
    }
  };

  public SkaldMusicService(Context context, final Provider... providers) {
    this.providers.addAll(Arrays.asList(providers));
    this.context = context.getApplicationContext();
    LocalBroadcastManager
        .getInstance(context.getApplicationContext())
        .registerReceiver(messageReceiver, new IntentFilter(INTENT_ACTION));
  }

  public void setSource(SkaldTrack skaldTrack) {
    currentTrack = skaldTrack;
  }

  public void setSource(SkaldPlaylist skaldPlaylist) {
    currentPlaylist = skaldPlaylist;
  }

  public void prepare() {
    skaldAuthData = getSkaldAuthStore().restore(context);
    if(skaldAuthData == null) {
      for(AuthErrorListener authErrorListener : authErrorListeners) {
        authErrorListener.onAuthError(getAuthError());
      }
    }
    else {
      player = getPlayer();
    }
  }

  public void prepareAsync() {

  }

  public void playTrack() {
    player.play(currentTrack);
  }

  public void playPlaylist() {
    player.play(currentPlaylist);
  }

  public void pause() {

  }

  public void resume() {

  }

  public void stop() {

  }

  public void release() {
  }

  public void addOnErrorListener(OnErrorListener onErrorListener) {
    onErrorListeners.add(onErrorListener);
  }

  public void addOnPreparedListener(OnPreparedListener onPreparedListener) {
    onPreparedListeners.add(onPreparedListener);
  }

  public void removeOnErrorListener(OnErrorListener onErrorListener) {
    onErrorListeners.remove(onErrorListener);
  }

  public void removeOnPreparedListener(OnPreparedListener onPreparedListener) {
    onPreparedListeners.remove(onPreparedListener);
  }

  public void addAuthErrorListener(AuthErrorListener authErrorListener) {
    authErrorListeners.add(authErrorListener);
  }

  public void removeAuthErrorListener(AuthErrorListener authErrorListener) {
    authErrorListeners.remove(authErrorListener);
  }

  public Observable<SkaldTrack> searchTrack(String query) {
    return getApiCalls().searchForTracks(query);
  }

  public Observable<SkaldPlaylist> searchPlayList(String query) {
    return getApiCalls().searchForPlaylists(query);
  }

  private Player getPlayer() {
    //for now assume existing of only one provider
    Player player = providers.get(0)
        .getPlayerFactory()
        .getPlayerFor(currentTrack, skaldAuthData);

    player.addLoginFailedListener(new LoginFailedListener() {
      @Override
      public void onLoginFailed() {
        for(AuthErrorListener authErrorListener : authErrorListeners) {
          authErrorListener.onAuthError(getAuthError());
          prepare();
        }
      }
    });

    player.addPlayerReadyListener(new OnPlayerReadyListener() {
      @Override
      public void onPlayerReady(Player player) {
        for(OnPreparedListener onPreparedListener: onPreparedListeners){
          onPreparedListener.onPrepared(SkaldMusicService.this);
        }
      }
    });

    return player;
  }

  private SkaldAuthStore getSkaldAuthStore() {
    return providers.get(0)
        .getSkaldAuthStoreFactory()
        .getSkaldAuthStore();
  }

  private AuthError getAuthError() {
    return providers.get(0)
        .getAuthErrorFactory()
        .getAuthError();
  }

  private ApiCalls getApiCalls() {
    return providers.get(0)
        .getApiCallsFactory()
        .getApiCalls(skaldAuthData);
  }
}