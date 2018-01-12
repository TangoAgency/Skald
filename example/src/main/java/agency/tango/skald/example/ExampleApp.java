package agency.tango.skald.example;

import android.app.Application;
import android.net.Uri;
import android.os.Environment;
import com.facebook.stetho.Stetho;
import java.util.Arrays;
import agency.tango.skald.core.Skald;
import agency.tango.skald.deezer.provider.DeezerProvider;
import agency.tango.skald.exoplayer.provider.ExoPlayerProvider;
import agency.tango.skald.exoplayer.services.ExoPlayerDefaultSearchService;
import agency.tango.skald.spotify.provider.SpotifyProvider;

public class ExampleApp extends Application {
  public static final String SPOTIFY_CLIENT_ID = "8c43f75741454312adbbbb9d5ac6cb5b";
  public static final String SPOTIFY_REDIRECT_URI = "spotify-example-marcin-first-app://callback";
  public static final String SPOTIFY_CLIENT_SECRET = "f4becaa46ff247e0b9d90d4ab853b2a9";
  public static final String DEEZER_CLIENT_ID = "250322";

  @Override
  public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this);

    initSkald();
  }

  private void initSkald() {
    SpotifyProvider spotifyProvider = new SpotifyProvider(this, SPOTIFY_CLIENT_ID,
        SPOTIFY_REDIRECT_URI, SPOTIFY_CLIENT_SECRET);
    DeezerProvider deezerProvider = new DeezerProvider(this, DEEZER_CLIENT_ID);

    String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
    Uri uri = Uri.parse(absolutePath);
    uri = Uri.withAppendedPath(uri, Environment.DIRECTORY_DOWNLOADS);

    ExoPlayerProvider exoPlayerProvider = new ExoPlayerProvider(this,
        new ExoPlayerDefaultSearchService(Arrays.asList((uri.getPath()))));

    Skald.with(spotifyProvider, deezerProvider, exoPlayerProvider);
  }
}
