package agency.tango.skald.spotify;

import android.content.Context;

import agency.tango.skald.core.AuthException;
import agency.tango.skald.core.Player;
import agency.tango.skald.core.Provider;
import agency.tango.skald.core.ProviderName;
import agency.tango.skald.core.SearchService;
import agency.tango.skald.core.SkaldAuthStore;
import agency.tango.skald.core.factories.PlayerFactory;
import agency.tango.skald.core.factories.SearchServiceFactory;
import agency.tango.skald.core.factories.SkaldAuthStoreFactory;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.spotify.models.SpotifyPlaylist;
import agency.tango.skald.spotify.models.SpotifyTrack;

public class SpotifyProvider extends Provider {
  @ProviderName
  public static final String SPOTIFY_PROVIDER = "spotify";

  static final String EXTRA_CLIENT_ID = "SPOTIFY_CLIENT_ID";
  static final String EXTRA_REDIRECT_URI = "SPOTIFY_REDIRECT_URI";
  static final String EXTRA_CLIENT_SECRET = "SPOTIFY_CLIENT_SECRET";

  private final Context context;
  private final String clientId;
  private final String redirectUri;
  private final String clientSecret;

  public SpotifyProvider(Context context, String clientId, String redirectUri,
      String clientSecret) {
    this.context = context;
    this.clientId = clientId;
    this.redirectUri = redirectUri;
    this.clientSecret = clientSecret;
  }

  @Override
  public String getProviderName() {
    return SPOTIFY_PROVIDER;
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
  public SearchServiceFactory getSearchServiceFactory() {
    return new SpotifySearchServiceFactory(context, this);
  }

  @Override
  public boolean canHandle(SkaldPlaylist skaldPlaylist) {
    return skaldPlaylist instanceof SpotifyPlaylist;
  }

  @Override
  public boolean canHandle(SkaldTrack skaldTrack) {
    return skaldTrack instanceof SpotifyTrack;
  }

  String getClientId() {
    return clientId;
  }

  String getRedirectUri() {
    return redirectUri;
  }

  String getClientSecret() {
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

  private static class SpotifySearchServiceFactory extends SearchServiceFactory {
    private final Context context;
    private final SkaldAuthStore skaldAuthDataStore;
    private final SpotifyProvider spotifyProvider;

    private SpotifySearchServiceFactory(Context context, SpotifyProvider spotifyProvider) {
      this.context = context;
      this.skaldAuthDataStore = new SpotifyAuthStore(spotifyProvider);
      this.spotifyProvider = spotifyProvider;
    }

    @Override
    public SearchService getSearchService() throws AuthException {
      SpotifyAuthData spotifyAuthData = (SpotifyAuthData) skaldAuthDataStore.restore(context);
      return new SpotifySearchService(context, spotifyAuthData, spotifyProvider);
    }
  }
}
