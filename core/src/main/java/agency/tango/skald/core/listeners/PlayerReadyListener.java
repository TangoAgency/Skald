package agency.tango.skald.core.listeners;

import agency.tango.skald.core.Player;

public interface PlayerReadyListener {
  void onPlayerReady(Player player);
  void onError(); // przekazac cos fajnego dla koncowego proramity
}
