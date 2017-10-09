package agency.tango.skald.core.factories;

import agency.tango.skald.core.exceptions.AuthException;
import agency.tango.skald.core.Player;

public abstract class PlayerFactory {
  public abstract Player getPlayer() throws AuthException;
}
