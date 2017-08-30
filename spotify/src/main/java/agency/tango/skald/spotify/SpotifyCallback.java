package agency.tango.skald.spotify;

import android.util.Log;

import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;

class SpotifyCallback implements Player.OperationCallback {
  private static final String TAG = "Spotify";

  @Override
  public void onSuccess() {
    Log.i(TAG, "Operation succeed");
  }

  @Override
  public void onError(Error error) {
    Log.e(TAG, "Operation failed");
  }
}
