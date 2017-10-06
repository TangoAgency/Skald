package agency.tango.skald.core;

import agency.tango.skald.core.factories.PlayerFactory;
import agency.tango.skald.core.factories.SearchServiceFactory;
import agency.tango.skald.core.factories.SkaldAuthStoreFactory;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;

public abstract class Provider {
  public static final String SPOTIFY_PROVIDER = "spotify";
  public static final String DEEZER_PROVIDER = "deezer";

  public abstract String getProviderName();

  public abstract PlayerFactory getPlayerFactory();

  public abstract SkaldAuthStoreFactory getSkaldAuthStoreFactory();

  public abstract SearchServiceFactory getSearchServiceFactory();

  public abstract boolean canHandle(SkaldPlaylist skaldPlaylist);

  public abstract boolean canHandle(SkaldTrack skaldTrack);
}
