package agency.tango.skald.core;

import android.content.Context;

public interface SkaldAuthStore {
  void save(Context context, SkaldAuthData skaldAuthData);

  SkaldAuthData restore(Context context) throws AuthException;
}
