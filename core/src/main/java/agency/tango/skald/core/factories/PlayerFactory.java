package agency.tango.skald.core.factories;

import agency.tango.skald.core.Player;
import agency.tango.skald.core.exceptions.AuthException;
import agency.tango.skald.core.listeners.OnErrorListener;

public abstract class PlayerFactory {
  public abstract Player getPlayer(OnErrorListener onErrorListener) throws AuthException;
}
