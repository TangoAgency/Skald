package agency.tango.skald.example;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import agency.tango.skald.R;
import agency.tango.skald.core.AuthError;
import agency.tango.skald.core.SkaldMusicService;
import agency.tango.skald.core.listeners.AuthErrorListener;
import agency.tango.skald.core.listeners.OnPreparedListener;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.spotify.SpotifyProvider;
import agency.tango.skald.spotify.models.SpotifyTrack;

public class MainActivity extends Activity {
  private static final String TAG = MainActivity.class.getSimpleName();
  public static final String SPOTIFY_CLIENT_ID = "8c43f75741454312adbbbb9d5ac6cb5b";
  public static final String SPOTIFY_REDIRECT_URI = "spotify-example-marcin-first-app://callback";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    SpotifyProvider spotifyProvider = new SpotifyProvider(this, SPOTIFY_CLIENT_ID,
        SPOTIFY_REDIRECT_URI);

    SkaldMusicService skaldMusicService = new SkaldMusicService(this, spotifyProvider);

    skaldMusicService.addAuthErrorListener(new AuthErrorListener() {
      @Override
      public void onAuthError(AuthError authError) {
        if(authError.hasResolution()) {
          Intent intent = authError.getResolution();
          startActivity(intent);
        }
      }
    });

    skaldMusicService.addOnPreparedListener(new OnPreparedListener() {
      @Override
      public void onPrepared(SkaldMusicService skaldMusicService) {
        Log.d(TAG, "Inside onPreparedList");

        List<SkaldTrack> skaldTracks;
        skaldTracks = skaldMusicService.searchTrack("abba");

        //skaldMusicService.setSource(skaldTracks.get(0));
        skaldMusicService.play();
      }
    });

    Uri spotifyUri = Uri.parse(
        "skald://spotify/track/spotify:user:spotify:playlist:37i9dQZF1DX8vpLK1FoEw3");
    final SkaldTrack skaldTrack = new SpotifyTrack(spotifyUri);

    skaldMusicService.setSource(skaldTrack);
    skaldMusicService.prepare();


    //Player player = spotifyProvider
    //    .getPlayerFactory()
    //    .getPlayerFor(new SpotifyTrack(spotifyUri));
    //
    //player.play(new SpotifyTrack(spotifyUri));

    //Button spotifyButton = (Button) findViewById(R.id.button_spotify);
    //spotifyButton.setOnClickListener(new View.OnClickListener() {
    //  @Override
    //  public void onClick(View v) {
    //    Intent intent = new Intent(MainActivity.this, SpotifyActivity.class);
    //    startActivity(intent);
    //  }
    //});
    //
    //Button deezerButton = (Button) findViewById(R.id.button_deezer);
    //deezerButton.setOnClickListener(new View.OnClickListener() {
    //  @Override
    //  public void onClick(View v) {
    //    Intent intent = new Intent(MainActivity.this, DeezerActivity.class);
    //    startActivity(intent);
    //  }
    //});
  }
}
