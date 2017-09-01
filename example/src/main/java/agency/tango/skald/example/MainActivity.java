package agency.tango.skald.example;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import agency.tango.skald.R;
import agency.tango.skald.core.SkaldMusicService;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.spotify.SpotifyAuthorizationActivity;
import agency.tango.skald.spotify.SpotifyProvider;

import static agency.tango.skald.spotify.SpotifyProvider.EXTRA_CLIENT_ID;
import static agency.tango.skald.spotify.SpotifyProvider.EXTRA_REDIRECT_URI;
import static agency.tango.skald.spotify.SpotifyProvider.SPOTIFY_CLIENT_ID;
import static agency.tango.skald.spotify.SpotifyProvider.SPOTIFY_REDIRECT_URI;

public class MainActivity extends Activity {
  private static final String TAG = MainActivity.class.getSimpleName();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Intent intent = new Intent(this, SpotifyAuthorizationActivity.class);
    intent.putExtra(EXTRA_CLIENT_ID, SPOTIFY_CLIENT_ID);
    intent.putExtra(EXTRA_REDIRECT_URI, SPOTIFY_REDIRECT_URI);
    startActivity(intent);

    SpotifyProvider spotifyProvider = new SpotifyProvider(this, SPOTIFY_CLIENT_ID,
        SPOTIFY_REDIRECT_URI);

    Uri spotifyUri = Uri.parse(
        "skald://spotify/track/spotify:user:spotify:playlist:37i9dQZF1DX8vpLK1FoEw3");
    final SkaldTrack skaldTrack = new SkaldTrack(spotifyUri);

    SkaldMusicService skaldMusicService = new SkaldMusicService(this, spotifyProvider);
    //skaldMusicService.setSource(skaldTrack);
    //skaldMusicService.addOnPreparedListener(new OnPreparedListener() {
    //  @Override
    //  public void onPrepared(SkaldMusicService skaldMusicService) {
    //    Log.d(TAG, "onPrepared");
    //    skaldMusicService.play();
    //  }
    //});

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
