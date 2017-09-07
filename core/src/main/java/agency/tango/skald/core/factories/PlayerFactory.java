package agency.tango.skald.core.factories;

import agency.tango.skald.core.Player;
import agency.tango.skald.core.SkaldAuthData;
import agency.tango.skald.core.models.SkaldTrack;

public abstract class PlayerFactory {
  public abstract Player getPlayerFor(SkaldTrack track, SkaldAuthData skaldAuthData);
}
