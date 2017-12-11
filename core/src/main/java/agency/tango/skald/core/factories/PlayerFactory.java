package agency.tango.skald.core.factories;

import agency.tango.skald.core.Player;
import agency.tango.skald.core.exceptions.AuthException;

public abstract class PlayerFactory {
  public abstract Player getPlayer() throws AuthException;
}
