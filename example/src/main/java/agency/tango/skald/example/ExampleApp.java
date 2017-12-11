package agency.tango.skald.example;

import android.app.Application;
import agency.tango.skald.core.Skald;
import agency.tango.skald.deezer.provider.DeezerProvider;
import agency.tango.skald.spotify.provider.SpotifyProvider;

public class ExampleApp extends Application {
  public static final String SPOTIFY_CLIENT_ID = "8c43f75741454312adbbbb9d5ac6cb5b";
  public static final String SPOTIFY_REDIRECT_URI = "spotify-example-marcin-first-app://callback";
  public static final String SPOTIFY_CLIENT_SECRET = "f4becaa46ff247e0b9d90d4ab853b2a9";
  public static final String DEEZER_CLIENT_ID = "250322";

  @Override
  public void onCreate() {
    super.onCreate();

    initSkald();
  }

  private void initSkald() {
    SpotifyProvider spotifyProvider = new SpotifyProvider(this, SPOTIFY_CLIENT_ID,
        SPOTIFY_REDIRECT_URI, SPOTIFY_CLIENT_SECRET);
    DeezerProvider deezerProvider = new DeezerProvider(this, DEEZER_CLIENT_ID);

    Skald.with(spotifyProvider, deezerProvider);
  }
}
