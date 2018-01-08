package agency.tango.skald.core;

import android.support.annotation.NonNull;
import agency.tango.skald.core.callbacks.SkaldOperationCallback;
import agency.tango.skald.core.listeners.OnLoadingListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.models.SkaldPlayableEntity;

public interface Player {
  void play(@NonNull SkaldPlayableEntity skaldPlayableEntity,
      SkaldOperationCallback skaldOperationCallback);

  void stop(SkaldOperationCallback skaldOperationCallback);

  void pause(SkaldOperationCallback skaldOperationCallback);

  void resume(SkaldOperationCallback skaldOperationCallback);

  void release();

  boolean isPlaying();

  void addOnPlayerReadyListener(@NonNull OnPlayerReadyListener onPlayerReadyListener);

  void removeOnPlayerReadyListener(@NonNull OnPlayerReadyListener onPlayerReadyListener);

  void addOnPlaybackListener(@NonNull OnPlaybackListener onPlaybackListener);

  void removeOnPlaybackListener(@NonNull OnPlaybackListener onPlaybackListener);

  void addOnLoadingListener(@NonNull OnLoadingListener onLoadingListener);

  void removeOnLoadingListener(@NonNull OnLoadingListener onLoadingListener);
}
