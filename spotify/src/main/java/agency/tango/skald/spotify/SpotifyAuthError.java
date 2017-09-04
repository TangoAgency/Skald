package agency.tango.skald.spotify;

import android.content.Context;
import android.content.Intent;

import agency.tango.skald.core.AuthError;

import static agency.tango.skald.spotify.SpotifyProvider.EXTRA_CLIENT_ID;
import static agency.tango.skald.spotify.SpotifyProvider.EXTRA_REDIRECT_URI;

public class SpotifyAuthError extends AuthError {
  private final Context context;
  private final String clientId;
  private final String redirectUri;

  public SpotifyAuthError(Context context, String clientId, String redirectUri) {
    this.context = context.getApplicationContext();
    this.clientId = clientId;
    this.redirectUri = redirectUri;
  }

  @Override
  public Intent getResolution() {
    Intent intent = new Intent(context, SpotifyAuthActivity.class);
    intent.putExtra(EXTRA_CLIENT_ID, clientId);
    intent.putExtra(EXTRA_REDIRECT_URI, redirectUri);
    return intent;
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
