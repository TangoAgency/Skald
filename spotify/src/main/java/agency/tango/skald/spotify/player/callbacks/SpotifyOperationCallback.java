package agency.tango.skald.spotify.player.callbacks;

import android.util.Log;

import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;

public abstract class SpotifyOperationCallback implements Player.OperationCallback {
  private static final String TAG = SpotifyOperationCallback.class.getSimpleName();

  @Override
  public void onError(Error error) {
    Log.e(TAG, String.format("Operation failed %s", error.toString()));
  }
}
