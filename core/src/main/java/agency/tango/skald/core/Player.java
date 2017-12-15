package agency.tango.skald.core;

import agency.tango.skald.core.callbacks.SkaldOperationCallback;
import agency.tango.skald.core.listeners.OnLoadingListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.models.SkaldPlayableEntity;

public interface Player {
  void play(SkaldPlayableEntity skaldPlayableEntity,
      SkaldOperationCallback skaldOperationCallback);

  void stop(SkaldOperationCallback skaldOperationCallback);

  void pause(SkaldOperationCallback skaldOperationCallback);

  void resume(SkaldOperationCallback skaldOperationCallback);

  void release();

  boolean isPlaying();

  void addOnPlayerReadyListener(OnPlayerReadyListener onPlayerReadyListener);

  void removeOnPlayerReadyListener(OnPlayerReadyListener onPlayerReadyListener);

  void addOnPlaybackListener(OnPlaybackListener onPlaybackListener);

  void removeOnPlaybackListener(OnPlaybackListener onPlaybackListener);

  void addOnLoadingListener(OnLoadingListener onLoadingListener);

  void removeOnLoadingListener(OnLoadingListener onLoadingListener);
}
