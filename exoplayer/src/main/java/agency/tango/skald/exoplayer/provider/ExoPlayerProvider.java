package agency.tango.skald.exoplayer.provider;

import android.content.Context;

import agency.tango.skald.core.Player;
import agency.tango.skald.core.exceptions.AuthException;
import agency.tango.skald.core.factories.PlayerFactory;
import agency.tango.skald.core.factories.SearchServiceFactory;
import agency.tango.skald.core.factories.SkaldAuthStoreFactory;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.provider.Provider;
import agency.tango.skald.core.provider.ProviderName;
import agency.tango.skald.exoplayer.player.SkaldExoPlayer;

public class ExoPlayerProvider extends Provider {
  public static final ProviderName NAME = new ExoPlayerProviderName();

  private final Context context;

  public ExoPlayerProvider(Context context) {
    this.context = context;
  }

  @Override
  public ProviderName getProviderName() {
    return NAME;
  }

  @Override
  public PlayerFactory getPlayerFactory() {
    return new SkaldExoPlayerFactory(context);
  }

  @Override
  public SkaldAuthStoreFactory getSkaldAuthStoreFactory() {
    return null;
  }

  @Override
  public SearchServiceFactory getSearchServiceFactory() {
    return null;
  }

  @Override
  public boolean canHandle(SkaldPlayableEntity skaldPlayableEntity) {
    return skaldPlayableEntity.getUri().getScheme().contains("http");
  }

  private class SkaldExoPlayerFactory extends PlayerFactory {
    private final Context context;

    public SkaldExoPlayerFactory(Context context) {
      this.context = context;
    }

    @Override
    public Player getPlayer() throws AuthException {
      return new SkaldExoPlayer(context);
    }
  }
}
