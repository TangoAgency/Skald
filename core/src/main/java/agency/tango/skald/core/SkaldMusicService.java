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
  public static final String INTENT_ACTION = "spotify_auth_action";
  private static final String TAG = SkaldMusicService.class.getSimpleName();

  private final List<OnPreparedListener> onPreparedListeners = new ArrayList<>();
  private final List<OnErrorListener> onErrorListeners = new ArrayList<>();
  private final List<Provider> providers = new ArrayList<>();
  private final Context context;
  //for now for simplicity only one player
  private Player player;
  private SkaldTrack currentTrack;
  private UriParser uriParser;


  private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String message = intent.getStringExtra("token");
      Log.d(TAG, "Got message: " + message);
    }
  };

  public SkaldMusicService(Context context, final Provider... providers) {
    this.providers.addAll(Arrays.asList(providers));
    this.context = context.getApplicationContext();
    LocalBroadcastManager
        .getInstance(context.getApplicationContext())
        .registerReceiver(mMessageReceiver, new IntentFilter(INTENT_ACTION));

    //for now assume existing of only one provider
    uriParser = this.providers.get(0).getParser();
  }

  public void setSource(SkaldTrack skaldTrack) {
    this.currentTrack = uriParser.parseSkaldTrack(skaldTrack);

    if(player == null) {
      player = SkaldMusicService.this.providers.get(0)
          .getPlayerFactory()
          .getPlayerFor(currentTrack);

      player.addPlayerReadyListener(new OnPlayerReadyListener() {
        @Override
        public void onPlayerReady(Player player) {
          for(OnPreparedListener onPreparedListener: onPreparedListeners){
            Log.d(TAG, "onPlayerReady");
            onPreparedListener.onPrepared(SkaldMusicService.this);
          }
        }
      });
    }
  }

  public void setSource(SkaldPlaylist skaldPlaylist) {
  }

  public void prepare() {

  }

  public void prepareAsync() {

  }

  public void play() {
    player.play(currentTrack);
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

  public List<SkaldTrack> searchTrack(String query) {
    return Collections.emptyList();
  }

  public List<SkaldPlaylist> searchPlayList(String query) {
    return Collections.emptyList();
  }

  //private SkaldTrack parseSkaldTrack(SkaldTrack skaldTrack) {
  //  Uri uri = skaldTrack.getUri();
  //  String authority = uri.getAuthority();
  //  if(authority.equals("spotify")) {
  //    return new SpotifyTrack(
  //
  //}
}