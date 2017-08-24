package agency.tango.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import agency.tango.core.listeners.ErrorListener;
import agency.tango.core.listeners.MetadataListener;
import agency.tango.core.listeners.PlaybackListener;
import agency.tango.core.models.SkaldPlaylist;
import agency.tango.core.models.SkaldTrack;

public class PlayerService extends Service implements Player {
  private final Player player;

  public PlayerService(Player player) {
    this.player = player;
  }

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

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
