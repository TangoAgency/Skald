package agency.tango.skald.spotify;

import android.content.Context;
import android.content.Intent;

import agency.tango.skald.core.AuthError;

public class SpotifyAuthError extends AuthError {

  private final Context context;

  public SpotifyAuthError(Context context) {
    this.context = context.getApplicationContext();
  }

  @Override
  public Intent getResolution() {
    return new Intent(context, SpotifyAuthActivity.class);
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
