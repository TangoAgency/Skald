package agency.tango.skald.deezer.authentication;

import android.content.Context;

import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.connect.SessionStore;

import agency.tango.skald.core.authentication.SkaldAuthData;
import agency.tango.skald.core.authentication.SkaldAuthStore;
import agency.tango.skald.core.exceptions.AuthException;
import agency.tango.skald.deezer.errors.DeezerAuthError;
import agency.tango.skald.deezer.exceptions.DeezerAuthException;
import agency.tango.skald.deezer.provider.DeezerProvider;

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
    if (!sessionStore.restore(deezerConnect, context)) {
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
