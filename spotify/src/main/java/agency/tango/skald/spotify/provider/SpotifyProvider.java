package agency.tango.skald.spotify.provider;

import android.content.Context;

import agency.tango.skald.core.Player;
import agency.tango.skald.core.SearchService;
import agency.tango.skald.core.UserService;
import agency.tango.skald.core.authentication.SkaldAuthStore;
import agency.tango.skald.core.exceptions.AuthException;
import agency.tango.skald.core.factories.PlayerFactory;
import agency.tango.skald.core.factories.ServicesFactory;
import agency.tango.skald.core.factories.SkaldAuthStoreFactory;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.provider.Provider;
import agency.tango.skald.core.provider.ProviderName;
import agency.tango.skald.spotify.api.SpotifyApi;
import agency.tango.skald.spotify.authentication.SpotifyAuthData;
import agency.tango.skald.spotify.authentication.SpotifyAuthStore;
import agency.tango.skald.spotify.models.SpotifyPlaylist;
import agency.tango.skald.spotify.models.SpotifyTrack;
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
  private final SpotifyApi.SpotifyApiImpl spotifyApi;

  public SpotifyProvider(Context context, String clientId, String redirectUri,
      String clientSecret) {
    this.context = context;
    this.clientId = clientId;
    this.redirectUri = redirectUri;
    this.clientSecret = clientSecret;
    spotifyApi = new SpotifyApi.SpotifyApiImpl(context, this);
  }

  @Override
  public ProviderName getProviderName() {
    return NAME;
  }

  @Override
  public PlayerFactory getPlayerFactory() {
    return new SpotifyPlayerFactory(context, this);
  }

  @Override
  public SkaldAuthStoreFactory getSkaldAuthStoreFactory() {
    return new SpotifyAuthStoreFactory(this);
  }

  @Override
  public ServicesFactory getServicesFactory() throws AuthException {
    SpotifyAuthStore spotifyAuthStore = new SpotifyAuthStore(this);
    SpotifyAuthData spotifyAuthData = (SpotifyAuthData) spotifyAuthStore.restore(context);
    spotifyApi.setRefreshToken(spotifyAuthData.getRefreshToken());
    return new SpotifyServicesFactory(spotifyApi);
  }

  @Override
  public boolean canHandle(SkaldPlayableEntity playableEntity) {
    return playableEntity instanceof SpotifyTrack || playableEntity instanceof SpotifyPlaylist;
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

  private static class SpotifyPlayerFactory extends PlayerFactory {
    private final Context context;
    private final SkaldAuthStore skaldAuthDataStore;
    private final SpotifyProvider spotifyProvider;

    private SpotifyPlayerFactory(Context context, SpotifyProvider spotifyProvider) {
      this.context = context;
      this.skaldAuthDataStore = new SpotifyAuthStore(spotifyProvider);
      this.spotifyProvider = spotifyProvider;
    }

    @Override
    public Player getPlayer() throws AuthException {
      SpotifyAuthData spotifyAuthData = (SpotifyAuthData) skaldAuthDataStore.restore(context);
      return new SkaldSpotifyPlayer(context, spotifyAuthData, spotifyProvider);
    }
  }

  private static class SpotifyAuthStoreFactory extends SkaldAuthStoreFactory {
    private final SpotifyProvider spotifyProvider;

    private SpotifyAuthStoreFactory(SpotifyProvider spotifyProvider) {
      this.spotifyProvider = spotifyProvider;
    }

    @Override
    public SkaldAuthStore getSkaldAuthStore() {
      return new SpotifyAuthStore(spotifyProvider);
    }
  }

  private static class SpotifyServicesFactory extends ServicesFactory {
    private final SpotifyApi.SpotifyApiImpl spotifyApi;

    private SpotifyServicesFactory(SpotifyApi.SpotifyApiImpl spotifyApi) {
      this.spotifyApi = spotifyApi;
    }

    @Override
    public SearchService getSearchService() {
      return new SpotifySearchService(spotifyApi);
    }

    @Override
    public UserService getUserService() {
      return new SpotifyUserService(spotifyApi);
    }
  }
}
