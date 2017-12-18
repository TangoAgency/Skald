package agency.tango.skald.exoplayer.player;

import android.content.Context;
import android.os.Handler;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.List;
import agency.tango.skald.core.Player;
import agency.tango.skald.core.callbacks.SkaldOperationCallback;
import agency.tango.skald.core.listeners.OnErrorListener;
import agency.tango.skald.core.listeners.OnLoadingListener;
import agency.tango.skald.core.listeners.OnPlaybackListener;
import agency.tango.skald.core.listeners.OnPlayerReadyListener;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.exoplayer.models.ExoPlayerPlaylist;
import agency.tango.skald.exoplayer.models.ExoPlayerTrack;
import agency.tango.skald.exoplayer.player.listeners.PlayerEventsListener;
import okhttp3.OkHttpClient;

public class SkaldExoPlayer implements Player {
  private static final String appName = "Skald";
  private final SimpleExoPlayer exoPlayer;
  private final DataSource.Factory dataSourceFactory;
  private final ExtractorsFactory extractorsFactory;
  private final List<OnPlayerReadyListener> onPlayerReadyListeners = new ArrayList<>();
  private final List<OnPlaybackListener> onPlaybackListeners = new ArrayList<>();
  private final List<OnLoadingListener> onLoadingListeners = new ArrayList<>();

  public SkaldExoPlayer(final Context context, OnErrorListener onErrorListener,
      final OkHttpClient okHttpClient) {
    Handler mainHandler = new Handler(context.getMainLooper());

    final DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    TrackSelection.Factory trackSelectionFactory =
        new AdaptiveTrackSelection.Factory(bandwidthMeter);
    DefaultTrackSelector trackSelector =
        new DefaultTrackSelector(trackSelectionFactory);

    exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

    dataSourceFactory = new DataSource.Factory() {
      @Override
      public DataSource createDataSource() {
        return new DefaultDataSource(context, bandwidthMeter,
            new OkHttpDataSource(okHttpClient, Util.getUserAgent(context, appName), null,
                bandwidthMeter));
      }
    };
    extractorsFactory = new DefaultExtractorsFactory();

    exoPlayer.addListener(
        new PlayerEventsListener(mainHandler, onPlaybackListeners, onLoadingListeners,
            onErrorListener, exoPlayer, trackSelector));
  }

  @Override
  public void play(SkaldPlayableEntity skaldPlayableEntity,
      SkaldOperationCallback skaldOperationCallback) {
    MediaSource mediaSource = null;
    if (skaldPlayableEntity instanceof SkaldTrack) {
      mediaSource = new ExtractorMediaSource(skaldPlayableEntity.getUri(),
          dataSourceFactory, extractorsFactory, null, null);
    } else if (skaldPlayableEntity instanceof SkaldPlaylist) {
      List<MediaSource> mediaSources = new ArrayList<>();
      for (ExoPlayerTrack exoPlayerTrack : ((ExoPlayerPlaylist) skaldPlayableEntity).getTracks()) {
        mediaSources.add(new ExtractorMediaSource(exoPlayerTrack.getUri(), dataSourceFactory,
            extractorsFactory, null, null));
      }

      MediaSource[] mediaSourcesArray = new MediaSource[mediaSources.size()];
      mediaSourcesArray = mediaSources.toArray(mediaSourcesArray);
      mediaSource = new ConcatenatingMediaSource(mediaSourcesArray);
    }

    exoPlayer.prepare(mediaSource);

    if (!isPlaying()) {
      exoPlayer.setPlayWhenReady(true);
    }
    skaldOperationCallback.onSuccess();
  }

  @Override
  public void stop(SkaldOperationCallback skaldOperationCallback) {
    exoPlayer.stop();
    skaldOperationCallback.onSuccess();
  }

  @Override
  public void pause(SkaldOperationCallback skaldOperationCallback) {
    if (isPlaying()) {
      exoPlayer.setPlayWhenReady(false);
    }
    skaldOperationCallback.onSuccess();
  }

  @Override
  public void resume(SkaldOperationCallback skaldOperationCallback) {
    if (!isPlaying()) {
      exoPlayer.setPlayWhenReady(true);
    }
    skaldOperationCallback.onSuccess();
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
  public void removeOnPlayerReadyListener(OnPlayerReadyListener onPlayerReadyListener) {
    onPlayerReadyListeners.remove(onPlayerReadyListener);
  }

  @Override
  public void addOnPlaybackListener(OnPlaybackListener onPlaybackListener) {
    onPlaybackListeners.add(onPlaybackListener);
  }

  @Override
  public void removeOnPlaybackListener(OnPlaybackListener onPlaybackListener) {
    onPlaybackListeners.remove(onPlaybackListener);
  }

  @Override
  public void addOnLoadingListener(OnLoadingListener onLoadingListener) {
    onLoadingListeners.add(onLoadingListener);
  }

  @Override
  public void removeOnLoadingListener(OnLoadingListener onLoadingListener) {
    onLoadingListeners.remove(onLoadingListener);
  }
}
