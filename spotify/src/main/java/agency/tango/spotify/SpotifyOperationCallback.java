package agency.tango.spotify;

import android.util.Log;

import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;

public class SpotifyOperationCallback implements Player.OperationCallback {
  @Override
  public void onSuccess() {
    Log.i("SpotifyOperation", "Operation succeeded");
  }

  @Override
  public void onError(Error error) {
    Log.e("SpotifyOperation", String.format("Operation failed %s", error));
  }
}
