package agency.tango.skald.deezer.errors;

import android.content.Context;
import android.content.Intent;
import agency.tango.skald.core.errors.AuthError;
import agency.tango.skald.deezer.authentication.DeezerAuthActivity;

import static agency.tango.skald.deezer.provider.DeezerProvider.EXTRA_CLIENT_ID;

public class DeezerAuthError extends AuthError {
  private final Context context;
  private final String clientId;

  public DeezerAuthError(Context context, String clientId) {
    this.context = context;
    this.clientId = clientId;
  }

  @Override
  public Intent getResolution() {
    Intent intent = new Intent(context, DeezerAuthActivity.class);
    intent.putExtra(EXTRA_CLIENT_ID, clientId);
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
