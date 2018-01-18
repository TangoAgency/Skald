package agency.tango.skald.spotify.provider;

import android.content.Context;
import android.support.annotation.NonNull;
import agency.tango.skald.core.Player;
import agency.tango.skald.core.SearchService;
import agency.tango.skald.core.UserService;
import agency.tango.skald.core.authentication.SkaldAuthStore;
import agency.tango.skald.core.exceptions.AuthException;
import agency.tango.skald.core.factories.PlayerFactory;
import agency.tango.skald.core.factories.SearchServiceFactory;
import agency.tango.skald.core.factories.SkaldAuthStoreFactory;
import agency.tango.skald.core.factories.UserServiceFactory;
import agency.tango.skald.core.listeners.OnErrorListener;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.provider.Provider;
import agency.tango.skald.core.provider.ProviderName;
import agency.tango.skald.core.provider.UriValidator;
import agency.tango.skald.spotify.api.SpotifyApi;
import agency.tango.skald.spotify.authentication.SpotifyAuthData;
import agency.tango.skald.spotify.authentication.SpotifyAuthStore;
import agency.tango.skald.spotify.player.SkaldSpotifyPlayer;
import agency.tango.skald.spotify.services.SpotifySearchService;
import agency.tango.skald.spotify.services.SpotifyUserService;

public class SpotifyProvider extends Provider {
  public static final ProviderName NAME = new SpotifyProviderName();
  public static final String EXTRA_CLIENT_ID = "SPOTIFY_CLIENT_ID";
  public static final String EXTRA_REDIRECT_URI = "SPOTIFY_REDIRECT_URI";
  public static final String EXTRA_CLIENT_SECRET = "SPOTIFY_CLIENT_SECRET";

  private final Context context;
  private final String clientId;
  private final String redirectUri;
  private final String clientSecret;
  private final SpotifyAuthStore spotifyAuthStore;
  private final SpotifyApi.SpotifyApiImpl spotifyApi;

  public SpotifyProvider(Context context, String clientId, String redirectUri,
      String clientSecret) {
    this.context = context;
    this.clientId = clientId;
    this.redirectUri = redirectUri;
    this.clientSecret = clientSecret;
    spotifyApi = new SpotifyApi.SpotifyApiImpl(context, this);
    spotifyAuthStore = new SpotifyAuthStore(this);
  }

  @NonNull
  @Override
  public ProviderName getProviderName() {
    return NAME;
  }

  @NonNull
  @Override
  public PlayerFactory getPlayerFactory() {
    return new SpotifyPlayerFactory(context, this, spotifyAuthStore);
  }

  @NonNull
  @Override
  public SkaldAuthStoreFactory getSkaldAuthStoreFactory() {
    return new SpotifyAuthStoreFactory(this);
  }

  @NonNull
  @Override
  public SearchServiceFactory getSearchServiceFactory() {
    return new SpotifySearchServiceFactory(this);
  }

  @NonNull
  @Override
  public UserServiceFactory getUserServiceFactory() {
    return new SpotifyUserServiceFactory(this);
  }

  @Override
  public boolean canHandle(@NonNull SkaldPlayableEntity skaldPlayableEntity) {
    return UriValidator.validate(skaldPlayableEntity, NAME.getName());
  }

  public String getClientId() {
    return clientId;
  }

  public String getRedirectUri() {
    return redirectUri;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  SpotifyApi.SpotifyApiImpl getSpotifyApi() throws AuthException {
    SpotifyAuthData spotifyAuthData = (SpotifyAuthData) spotifyAuthStore.restore(context);
    spotifyApi.setRefreshToken(spotifyAuthData.getRefreshToken());
    return spotifyApi;
  }

  private static class SpotifyPlayerFactory extends PlayerFactory {
    private final Context context;
    private final SpotifyAuthStore spotifyAuthStore;
    private final SpotifyProvider spotifyProvider;

    private SpotifyPlayerFactory(Context context, SpotifyProvider spotifyProvider,
        SpotifyAuthStore spotifyAuthStore) {
      this.context = context;
      this.spotifyProvider = spotifyProvider;
      this.spotifyAuthStore = spotifyAuthStore;
    }

    @NonNull
    @Override
    public Player getPlayer(OnErrorListener onErrorListener) throws AuthException {
      SpotifyAuthData spotifyAuthData = (SpotifyAuthData) spotifyAuthStore.restore(context);
      return new SkaldSpotifyPlayer(context, spotifyAuthData, spotifyProvider, onErrorListener);
    }
  }

  private static class SpotifyAuthStoreFactory extends SkaldAuthStoreFactory {
    private final SpotifyProvider spotifyProvider;

    private SpotifyAuthStoreFactory(SpotifyProvider spotifyProvider) {
      this.spotifyProvider = spotifyProvider;
    }

    @NonNull
    @Override
    public SkaldAuthStore getSkaldAuthStore() {
      return new SpotifyAuthStore(spotifyProvider);
    }
  }

  private static class SpotifySearchServiceFactory extends SearchServiceFactory {
    private final SpotifyProvider spotifyProvider;

    private SpotifySearchServiceFactory(SpotifyProvider spotifyProvider) {
      this.spotifyProvider = spotifyProvider;
    }

    @NonNull
    @Override
    public SearchService getSearchService() throws AuthException {
      return new SpotifySearchService(spotifyProvider.getSpotifyApi());
    }
  }

  private static class SpotifyUserServiceFactory extends UserServiceFactory {
    private final SpotifyProvider spotifyProvider;

    private SpotifyUserServiceFactory(SpotifyProvider spotifyProvider) {
      this.spotifyProvider = spotifyProvider;
    }

    @NonNull
    @Override
    public UserService getUserService() throws AuthException {
      return new SpotifyUserService(spotifyProvider.getSpotifyApi());
    }
  }
}
