package agency.tango.skald.core.factories;

import agency.tango.skald.core.Player;
import agency.tango.skald.core.SkaldAuthData;

public abstract class PlayerFactory {
  public abstract Player getPlayer(SkaldAuthData skaldAuthData);
}
