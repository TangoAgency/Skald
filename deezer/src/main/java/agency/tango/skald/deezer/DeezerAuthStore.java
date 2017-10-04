package agency.tango.skald.deezer;

import android.content.Context;

import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.connect.SessionStore;

import agency.tango.skald.core.AuthException;
import agency.tango.skald.core.SkaldAuthData;
import agency.tango.skald.core.SkaldAuthStore;

public class DeezerAuthStore implements SkaldAuthStore {
  private final DeezerProvider deezerProvider;

  public DeezerAuthStore(DeezerProvider deezerProvider) {
    this.deezerProvider = deezerProvider;
  }

  @Override
  public void save(Context context, SkaldAuthData skaldAuthData) {
    SessionStore sessionStore = new SessionStore();
    sessionStore.save(((DeezerAuthData) skaldAuthData).getDeezerConnect(), context);
  }

  @Override
  public SkaldAuthData restore(Context context) throws AuthException {
    DeezerConnect deezerConnect = new DeezerConnect(deezerProvider.getClientId());
    SessionStore sessionStore = new SessionStore();
    if(!sessionStore.restore(deezerConnect, context)) {
      throw new DeezerAuthException("Cannot restore session", new DeezerAuthError(context,
          deezerProvider.getClientId()));
    }
    return new DeezerAuthData(deezerConnect);
  }

  @Override
  public void clear(Context context) {
    SessionStore sessionStore = new SessionStore();
    sessionStore.clear(context);
  }
}
