package agency.tango.skald.spotify;

import android.content.Context;

import agency.tango.skald.core.SearchService;
import agency.tango.skald.core.Player;
import agency.tango.skald.core.Provider;
import agency.tango.skald.core.SkaldAuthData;
import agency.tango.skald.core.SkaldAuthStore;
import agency.tango.skald.core.factories.SearchServiceFactory;
import agency.tango.skald.core.factories.PlayerFactory;
import agency.tango.skald.core.factories.SkaldAuthStoreFactory;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.spotify.models.SpotifyTrack;

public class SpotifyProvider extends Provider {
  public static final String EXTRA_CLIENT_ID = "SPOTIFY_CLIENT_ID";
  public static final String EXTRA_REDIRECT_URI = "SPOTIFY_REDIRECT_URI";
  public static final String EXTRA_CLIENT_SECRET = "SPOTIFY_CLIENT_SECRET";

  public static final String SPOTIFY_PROVIDER = "spotify";
  private final Context context;
  private final String clientId;
  private final String redirectUri;
  private final String clientSecret;

  public SpotifyProvider(Context context, String clientId, String redirectUri, String clientSecret) {
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
    return new SpotifyPlayerFactory(context, clientId, clientSecret);
  }

  @Override
  public SkaldAuthStoreFactory getSkaldAuthStoreFactory() {
    return new SpotifyAuthStoreFactory();
  }

  @Override
  public SearchServiceFactory getSearchServiceFactory() {
    return new SpotifySearchServiceFactory();
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
    private final String clientId;
    private final String clientSecret;

    private SpotifyPlayerFactory(Context context, String clientId, String clientSecret) {
      this.context = context;
      this.clientId = clientId;
      this.clientSecret = clientSecret;
    }

    @Override
    public Player getPlayerFor(SkaldTrack track, SkaldAuthData skaldAuthData) {
      if (track instanceof SpotifyTrack) {
        SpotifyAuthData spotifyAuthData = (SpotifyAuthData) skaldAuthData;
        return new SkaldSpotifyPlayer(context, spotifyAuthData, clientId, clientSecret);
      }
      return null;
    }
  }

  private static class SpotifyAuthStoreFactory extends SkaldAuthStoreFactory {
    @Override
    public SkaldAuthStore getSkaldAuthStore() {
      return new SpotifyAuthStore();
    }
  }

  private static class SpotifySearchServiceFactory extends SearchServiceFactory {
    @Override
    public SearchService getSearchService(SkaldAuthData skaldAuthData) {
      return new SpotifySearchService(skaldAuthData);
    }
  }
}
