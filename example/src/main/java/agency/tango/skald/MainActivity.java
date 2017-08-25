package agency.tango.skald;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import agency.tango.core.Player;
import agency.tango.core.PlayerConfig;
import agency.tango.core.listeners.PlayerReadyListener;
import agency.tango.core.models.SkaldTrack;
import agency.tango.spotify.SpotifyAuthenticator;
import agency.tango.spotify.SpotifySkaldPlayer;

public class MainActivity extends Activity {

  private final String CLIENT_ID = "8c43f75741454312adbbbb9d5ac6cb5b";
  private final String REDIRECT_URI = "spotify-example-marcin-first-app://callback";
  private SpotifyAuthenticator authenticator;
  SpotifySkaldPlayer player;

  private Button spotifyLoginButton;

  @Override
  protected void onCreate(Bundle savedInstance) {
    super.onCreate(savedInstance);
    setContentView(R.layout.activity_main);

    spotifyLoginButton = (Button) findViewById(R.id.spotify_login_btn);
    spotifyLoginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        authenticator.login(MainActivity.this);
      }
    });

    authenticator = new SpotifyAuthenticator(CLIENT_ID, REDIRECT_URI);
    player = new SpotifySkaldPlayer();

    player.addPlayerReadyListener(new PlayerReadyListener() {
      @Override
      public void onPlayerReady(Player player) {
        Log.d("SPOTIFY", " DZIA ALAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa");
        player.play(new SkaldTrack("spotify:user:spotify:playlist:37i9dQZF1DX8vpLK1FoEw3", "Song"));
      }

      @Override
      public void onError() {
        Log.e("SPOTIFY", "ERROR during logging in");
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    PlayerConfig playerConfig = authenticator.retrievePlayerConfigFromLogin(requestCode, resultCode,
        data);
    player.initializePlayer(playerConfig, CLIENT_ID, this);
  }
}
