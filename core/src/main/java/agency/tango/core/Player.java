package agency.tango.core;

import android.content.Context;

import agency.tango.core.listeners.ErrorListener;
import agency.tango.core.listeners.MetadataListener;
import agency.tango.core.listeners.PlaybackListener;
import agency.tango.core.listeners.PlayerReadyListener;
import agency.tango.core.models.SkaldPlaylist;
import agency.tango.core.models.SkaldTrack;

public interface Player {
  void play(SkaldTrack track);

  void play(SkaldPlaylist playlist);

  void stop();

  void pause();

  void resume();

  void release();

  void addErrorListener(ErrorListener errorListener);

  void addPlaybackListener(PlaybackListener playbackListener);

  void addMetadataListener(MetadataListener metadataListener);

  void addPlayerReadyListener(PlayerReadyListener playerReadyListener);

  void removeErrorListener(ErrorListener errorListener);

  void removePlaybackListener(PlaybackListener playbackListener);

  void removeMetadataListener(MetadataListener metadataListener);

  void removePlayerReadyListener(PlayerReadyListener playerReadyListener);

  void initializePlayer(PlayerConfig playerConfig, String clientId, Context context);
}
