package agency.tango.skald.spotify.player.callbacks;

import android.util.Log;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import agency.tango.skald.core.callbacks.SkaldOperationCallback;

public class SpotifyOperationCallback implements Player.OperationCallback {
  private static final String TAG = SpotifyOperationCallback.class.getSimpleName();
  private SkaldOperationCallback skaldOperationCallback;

  public SpotifyOperationCallback() {

  }

  public SpotifyOperationCallback(SkaldOperationCallback skaldOperationCallback) {
    this.skaldOperationCallback = skaldOperationCallback;
  }

  @Override
  public void onSuccess() {
    skaldOperationCallback.onSuccess();
  }

  @Override
  public void onError(Error error) {
    Log.e(TAG, String.format("Operation failed %s", error.toString()));
    skaldOperationCallback.onError();
  }
}
