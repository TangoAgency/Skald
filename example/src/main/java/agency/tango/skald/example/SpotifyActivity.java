package agency.tango.skald.example;
//
import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//
//import com.spotify.sdk.android.player.Spotify;
//
//import agency.tango.skald.R;
//import agency.tango.skald.core.Player;
//import agency.tango.skald.core.listeners.PlayerReadyListener;
//import agency.tango.skald.core.models.SkaldTrack;
//import agency.tango.skald.spotify.SpotifyAuthenticator;
//import agency.tango.skald.spotify.SpotifySkaldPlayer;
//
public class SpotifyActivity extends Activity {
//  private static final String REDIRECT_URI = "spotify-example-marcin-first-app://callback";
//  private static final String CLIENT_ID = "8c43f75741454312adbbbb9d5ac6cb5b";
//  private static final String TAG = "Spotify";
//
//  private Authenticator authenticator;
//  private SpotifySkaldPlayer player;
//
//  @Override
//  protected void onCreate(Bundle savedInstance) {
//    super.onCreate(savedInstance);
//    setContentView(R.layout.activity_spotify);
//    authenticator = new SpotifyAuthenticator(CLIENT_ID, REDIRECT_URI);
//
//    authenticator.setConfigCallback(new PlayerConfigCallback() {
//      @Override
//      public void configReady(PlayerConfig playerConfig) {
//        player.initializePlayer(playerConfig, CLIENT_ID, SpotifyActivity.this);
//      }
//
//      @Override
//      public void onError(String error) {
//        Log.e(TAG, String.format("Authentication error %s", error));
//      }
//    });
//
//    Button spotifyLoginButton = (Button) findViewById(R.id.spotify_login_btn);
//    spotifyLoginButton.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        authenticator.login(SpotifyActivity.this);
//      }
//    });
//
//    player = new SpotifySkaldPlayer();
//
//    player.addPlayerReadyListener(new PlayerReadyListener() {
//      @Override
//      public void onPlayerReady(Player player) {
//        player.play(new SkaldTrack("spotify:user:spotify:playlist:37i9dQZF1DX8vpLK1FoEw3", "Song"));
//        /// Uri.parse(skald://spotify/track/spotify:user:spotify:playlist:37i9dQZF1DX8vpLK1FoEw3)
//      }
//
//      @Override
//      public void onError() {
//        Log.e(TAG, "ERROR during logging in");
//      }
//    });
//  }
//
//  @Override
//  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    super.onActivityResult(requestCode, resultCode, data);
//    authenticator.onActivityResult(requestCode, resultCode, data);
//  }
//
//  @Override
//  protected void onDestroy() {
//    super.onDestroy();
//    player.pause();
//    Spotify.destroyPlayer(this);
//  }
}