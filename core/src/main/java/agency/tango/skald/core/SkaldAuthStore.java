package agency.tango.skald.core;

import android.content.Context;

public interface SkaldAuthStore {
  void save(SkaldAuthData skaldAuthData, Context context);

  SkaldAuthData restore(Context context);
}
