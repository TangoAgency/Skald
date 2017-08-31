package agency.tango.skald.spotify;

import agency.tango.skald.core.SkaldAuthorizationData;
import agency.tango.skald.core.SkaldAuthorizationStore;

public class SpotifyAuthorizationStore implements SkaldAuthorizationStore {
  @Override
  public void save(SkaldAuthorizationData skaldAuthorizationData) {
    // to do zapisac w shared preferences
  }

  @Override
  public SkaldAuthorizationData restore() {
    // restore from shared pref return null otherwise
    return null;
  }
}
