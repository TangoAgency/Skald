package agency.tango.skald.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;

public class SkaldMusicService {
  private static final String TAG = SkaldMusicService.class.getSimpleName();
  private final List<Provider> providers = new ArrayList<>();
  private final SkaldAuthorizationStore authorizationStore = null;
  private final Context context;
  private List<AuthorizationErrorListener> authorizationErrorListeners = new ArrayList<>();

  private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      // Get extra data included in the Intent
      String message = intent.getStringExtra("message");
      Log.d(TAG, "Got message: " + message);
    }
  };

  public SkaldMusicService(Context context, Provider... providers) {

    this.providers.addAll(Arrays.asList(providers));
    this.context = context.getApplicationContext();
    LocalBroadcastManager
        .getInstance(context.getApplicationContext())
        .registerReceiver(mMessageReceiver, new IntentFilter());
  }

  public void setSource(SkaldTrack skaldTrack) {
    //this.currentTrack = skaldTrack
  }

  public void setSource(SkaldPlaylist skaldPlaylist) {

  }

  public void prepare() {

  }

  public void prepareAsync() {

  }

  public void play() {

  }

  public void pause() {

  }

  public void resume() {

  }

  public void stop() {

  }

  public void release() {
  }

  public void addAuthorizationErrorListener(AuthorizationErrorListener authorizationErrorListener) {
    authorizationErrorListeners.add(authorizationErrorListener);
  }

  public void removeAuthorizationListener(AuthorizationErrorListener authorizationErrorListener) {
    authorizationErrorListeners.remove(authorizationErrorListener);
  }

  public List<SkaldTrack> searchTrack(String query) {
    return Collections.emptyList();
  }

  public List<SkaldPlaylist> searchPlayList(String query) {
    return Collections.emptyList();
  }
}