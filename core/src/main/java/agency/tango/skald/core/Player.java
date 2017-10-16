package agency.tango.skald.core;

import agency.tango.skald.core.listeners.OnLoadingListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.models.SkaldPlayableEntity;

public interface Player {
  void play(SkaldPlayableEntity skaldPlayableEntity);

  void stop();

  void pause();

  void resume();

  void release();

  void addOnPlayerReadyListener(OnPlayerReadyListener onPlayerReadyListener);

  void removeOnPlayerReadyListener();

  void addOnPlaybackListener(OnPlaybackListener onPlaybackListener);

  void removeOnPlaybackListener();

  void addOnLoadingListener(OnLoadingListener onLoadingListener);

  void removeOnLoadingListener();
}
