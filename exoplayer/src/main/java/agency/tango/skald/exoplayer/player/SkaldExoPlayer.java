package agency.tango.skald.exoplayer.player;

import android.content.Context;
import android.os.Handler;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

import agency.tango.skald.core.Player;
import agency.tango.skald.core.callbacks.SkaldOperationCallback;
import agency.tango.skald.core.listeners.OnLoadingListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.exoplayer.player.listeners.PlayerEventsListener;

public class SkaldExoPlayer implements Player {
  private final SimpleExoPlayer exoPlayer;
  private final DataSource.Factory dataSourceFactory;
  private final ExtractorsFactory extractorsFactory;
  private final List<OnPlayerReadyListener> onPlayerReadyListeners = new ArrayList<>();
  private final List<OnPlaybackListener> onPlaybackListeners = new ArrayList<>();
  private final List<OnLoadingListener> onLoadingListeners = new ArrayList<>();

  public SkaldExoPlayer(Context context) {
    Handler mainHandler = new Handler(context.getMainLooper());

    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    TrackSelection.Factory trackSelectionFactory =
        new AdaptiveTrackSelection.Factory(bandwidthMeter);
    TrackSelector trackSelector =
        new DefaultTrackSelector(trackSelectionFactory);

    exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

    dataSourceFactory = new DefaultDataSourceFactory(context,
        Util.getUserAgent(context, "Skald"));
    extractorsFactory = new DefaultExtractorsFactory();

    exoPlayer.addListener(
        new PlayerEventsListener(mainHandler, onPlaybackListeners, onLoadingListeners));
  }

  @Override
  public void play(SkaldPlayableEntity skaldPlayableEntity,
      SkaldOperationCallback skaldOperationCallback) {
    MediaSource trackSource = new ExtractorMediaSource(skaldPlayableEntity.getUri(),
        dataSourceFactory, extractorsFactory, null, null);

    exoPlayer.prepare(trackSource);

    if (!isPlaying()) {
      exoPlayer.setPlayWhenReady(true);
    }
  }

  @Override
  public void stop(SkaldOperationCallback skaldOperationCallback) {
    exoPlayer.stop();
  }

  @Override
  public void pause(SkaldOperationCallback skaldOperationCallback) {
    if (isPlaying()) {
      exoPlayer.setPlayWhenReady(false);
    }
  }

  @Override
  public void resume(SkaldOperationCallback skaldOperationCallback) {
    if (!isPlaying()) {
      exoPlayer.setPlayWhenReady(true);
    }
  }

  @Override
  public void release() {
    exoPlayer.release();
  }

  @Override
  public boolean isPlaying() {
    return exoPlayer.getPlayWhenReady();
  }

  @Override
  public void addOnPlayerReadyListener(OnPlayerReadyListener onPlayerReadyListener) {
    onPlayerReadyListeners.add(onPlayerReadyListener);

    for (OnPlayerReadyListener onPlayerReadyExistingListener : onPlayerReadyListeners) {
      onPlayerReadyExistingListener.onPlayerReady(this);
    }
  }

  @Override
  public void removeOnPlayerReadyListener() {
    onPlayerReadyListeners.remove(0);
  }

  @Override
  public void addOnPlaybackListener(OnPlaybackListener onPlaybackListener) {
    onPlaybackListeners.add(onPlaybackListener);
  }

  @Override
  public void removeOnPlaybackListener() {
    onPlaybackListeners.remove(0);
  }

  @Override
  public void addOnLoadingListener(OnLoadingListener onLoadingListener) {
    onLoadingListeners.add(onLoadingListener);
  }

  @Override
  public void removeOnLoadingListener() {
    onLoadingListeners.remove(0);
  }
}
