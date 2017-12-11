package agency.tango.skald.exoplayer.provider;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import agency.tango.skald.core.Player;
import agency.tango.skald.core.SearchService;
import agency.tango.skald.core.exceptions.AuthException;
import agency.tango.skald.core.factories.PlayerFactory;
import agency.tango.skald.core.factories.SearchServiceFactory;
import agency.tango.skald.core.factories.SkaldAuthStoreFactory;
import agency.tango.skald.core.listeners.OnErrorListener;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.provider.Provider;
import agency.tango.skald.core.provider.ProviderName;
import agency.tango.skald.exoplayer.player.SkaldExoPlayer;
import agency.tango.skald.exoplayer.services.ExoPlayerSearchService;
import okhttp3.OkHttpClient;

public class ExoPlayerProvider extends Provider {
  public static final ProviderName NAME = new ExoPlayerProviderName();

  private final Context context;
  private final ExoPlayerSearchService searchService;
  private final OkHttpClient okHttpClient;

  public ExoPlayerProvider(Context context, OkHttpClient okHttpClient,
      SearchService... searchServices) {
    this.context = context;
    this.okHttpClient = okHttpClient;
    this.searchService = new ExoPlayerSearchService();
    for (SearchService searchService : searchServices) {
      this.searchService.add(searchService);
    }
  }

  public ExoPlayerProvider(Context context, SearchService... searchServices) {
    this(context,
        new OkHttpClient
            .Builder()
            .addNetworkInterceptor(new StethoInterceptor())
            .build(),
        searchServices);
  }

  @Override
  public ProviderName getProviderName() {
    return NAME;
  }

  @Override
  public PlayerFactory getPlayerFactory() {
    return new SkaldExoPlayerFactory(context, okHttpClient);
  }

  @Override
  public SkaldAuthStoreFactory getSkaldAuthStoreFactory() {
    return null;
  }

  @Override
  public SearchServiceFactory getSearchServiceFactory() {
    return new ExoPlayerSearchServiceFactory(searchService);
  }

  @Override
  public boolean canHandle(SkaldPlayableEntity skaldPlayableEntity) {
    return skaldPlayableEntity.getUri().getScheme().contains("http") ||
        skaldPlayableEntity.getUri().getScheme().equals("file");
  }

  private static class ExoPlayerSearchServiceFactory extends SearchServiceFactory {
    private final SearchService searchService;

    public ExoPlayerSearchServiceFactory(SearchService searchService) {
      this.searchService = searchService;
    }

    @Override
    public SearchService getSearchService() throws AuthException {
      return searchService;
    }
  }

  private static class SkaldExoPlayerFactory extends PlayerFactory {
    private final Context context;
    private final OkHttpClient okHttpClient;

    public SkaldExoPlayerFactory(Context context, OkHttpClient okHttpClient) {
      this.context = context;
      this.okHttpClient = okHttpClient;
    }

    @Override
    public Player getPlayer(OnErrorListener onErrorListener) throws AuthException {
      return new SkaldExoPlayer(context, onErrorListener, okHttpClient);
    }
  }
}
