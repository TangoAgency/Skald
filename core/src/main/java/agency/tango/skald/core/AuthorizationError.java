package agency.tango.skald.core;

import android.content.Intent;

public abstract class AuthorizationError{
  public AuthorizationError() {
  }

  public abstract Intent getResolution();
  public abstract boolean hasResolution();
  public abstract int errorCode();
}
