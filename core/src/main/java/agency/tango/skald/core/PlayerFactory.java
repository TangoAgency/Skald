package agency.tango.skald.core;

import agency.tango.skald.core.models.SkaldTrack;

public abstract class PlayerFactory {
  public abstract Player getPlayerFor(SkaldTrack track);
}
