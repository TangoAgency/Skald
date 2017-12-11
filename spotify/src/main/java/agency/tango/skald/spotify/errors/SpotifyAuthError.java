package agency.tango.skald.spotify.errors;

import android.content.Context;
import android.content.Intent;
import agency.tango.skald.core.errors.AuthError;
import agency.tango.skald.spotify.authentication.SpotifyAuthActivity;

import static agency.tango.skald.spotify.provider.SpotifyProvider.EXTRA_CLIENT_ID;
import static agency.tango.skald.spotify.provider.SpotifyProvider.EXTRA_CLIENT_SECRET;
import static agency.tango.skald.spotify.provider.SpotifyProvider.EXTRA_REDIRECT_URI;

public class SpotifyAuthError extends AuthError {
  private final Context context;
  private final String clientId;
  private final String redirectUri;
  private final String clientSecret;

  public SpotifyAuthError(Context context, String clientId, String redirectUri,
      String clientSecret) {
    this.context = context.getApplicationContext();
    this.clientId = clientId;
    this.redirectUri = redirectUri;
    this.clientSecret = clientSecret;
  }

  @Override
  public Intent getResolution() {
    Intent intent = new Intent(context, SpotifyAuthActivity.class);
    intent.putExtra(EXTRA_CLIENT_ID, clientId);
    intent.putExtra(EXTRA_REDIRECT_URI, redirectUri);
    intent.putExtra(EXTRA_CLIENT_SECRET, clientSecret);
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
