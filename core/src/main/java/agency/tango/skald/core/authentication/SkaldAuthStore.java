package agency.tango.skald.core.authentication;

import android.content.Context;

import agency.tango.skald.core.exceptions.AuthException;

public interface SkaldAuthStore {
  void save(Context context, SkaldAuthData skaldAuthData);

  SkaldAuthData restore(Context context) throws AuthException;

  void clear(Context context);
}
