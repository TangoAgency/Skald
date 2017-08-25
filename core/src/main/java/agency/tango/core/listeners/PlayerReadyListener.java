package agency.tango.core.listeners;

import agency.tango.core.Player;

public interface PlayerReadyListener {
  void onPlayerReady(Player player);
  void onError(); // przekazac cos fajnego dla koncowego proramity
}
