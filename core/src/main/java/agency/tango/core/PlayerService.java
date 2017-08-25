package agency.tango.core;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import agency.tango.core.listeners.ErrorListener;
import agency.tango.core.listeners.MetadataListener;
import agency.tango.core.listeners.PlaybackListener;
import agency.tango.core.listeners.PlayerReadyListener;
import agency.tango.core.models.SkaldPlaylist;
import agency.tango.core.models.SkaldTrack;

public class PlayerService extends Service implements Player {
  private  Player player;

  //region Player
  @Override
  public void play(SkaldTrack track) {

  }

  @Override
  public void play(SkaldPlaylist playlist) {

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
  public void addErrorListener(ErrorListener errorListener) {

  }

  @Override
  public void addPlaybackListener(PlaybackListener playbackListener) {

  }

  @Override
  public void addMetadataListener(MetadataListener metadataListener) {

  }

  @Override
  public void addPlayerReadyListener(PlayerReadyListener playerReadyListener) {

  }

  @Override
  public void removeErrorListener(ErrorListener errorListener) {

  }

  @Override
  public void removePlaybackListener(PlaybackListener playbackListener) {

  }

  @Override
  public void removeMetadataListener(MetadataListener metadataListener) {

  }

  @Override
  public void removePlayerReadyListener(PlayerReadyListener playerReadyListener) {

  }

  @Override
  public void initializePlayer(PlayerConfig playerConfig, String clientId, Context context) {

  }
  //endregion

  //region Service
  @Override
  public void onCreate() {
    super.onCreate();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
  //endregion
}

