package agency.tango.skald.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import agency.tango.skald.R;
import agency.tango.skald.deezer.DeezerActivity;

public class MainActivity extends Activity {
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Button spotifyButton = (Button) findViewById(R.id.button_spotify);
    spotifyButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, SpotifyActivity.class);
        startActivity(intent);
      }
    });

    Button deezerButton = (Button) findViewById(R.id.button_deezer);
    deezerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, DeezerActivity.class);
        startActivity(intent);
      }
    });
  }
}
