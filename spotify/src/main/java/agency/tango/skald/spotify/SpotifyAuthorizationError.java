package agency.tango.skald.spotify;

import android.content.Context;
import android.content.Intent;

import agency.tango.skald.core.AuthorizationError;

public class SpotifyAuthorizationError extends AuthorizationError {

  private final Context context;

  public SpotifyAuthorizationError(Context context) {
    this.context = context.getApplicationContext();
  }

  @Override
  public Intent getResolution() {
    return new Intent(context, SpotifyAuthorizationActivity.class);
  }

  @Override
  public boolean hasResolution() {
    return true;
  }

  @Override
  public int errorCode() {
    return 0;
  }
}
