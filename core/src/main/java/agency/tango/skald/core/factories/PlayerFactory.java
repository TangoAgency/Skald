package agency.tango.skald.core.factories;

import android.support.annotation.NonNull;
import agency.tango.skald.core.Player;
import agency.tango.skald.core.exceptions.AuthException;
import agency.tango.skald.core.listeners.OnErrorListener;

public abstract class PlayerFactory {
  @NonNull
  public abstract Player getPlayer(OnErrorListener onErrorListener) throws AuthException;
}
