package agency.tango.skald.spotify;

import android.content.Context;

import agency.tango.skald.core.Player;
import agency.tango.skald.core.PlayerFactory;
import agency.tango.skald.core.Provider;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.spotify.models.SpotifyTrack;

public class SpotifyProvider extends Provider {

  public static final String SPOTIFY_CLIENT_ID = "8c43f75741454312adbbbb9d5ac6cb5b";
  public static final String SPOTIFY_REDIRECT_URI = "spotify-example-marcin-first-app://callback";

  public static final String EXTRA_CLIENT_ID = "SPOTIFY_CLIENT_ID";
  public static final String EXTRA_REDIRECT_URI = "SPOTIFY_REDIRECT_URI";

  public static final String SPOTIFY_PROVIDER = "spotify";
  private final Context context;
  private final String clientId;
  private final String redirectUri;

  public SpotifyProvider(Context context, String clientId, String redirectUri) {
    this.context = context;
    this.clientId = clientId;
    this.redirectUri = redirectUri;
  }

  @Override
  public String getProviderName() {
    return SPOTIFY_PROVIDER;
  }

  @Override
  public PlayerFactory getPlayerFactory() {
    return new SpotifyPlayerFactory(context, clientId);
  }

  public String getClientId() {
    return clientId;
  }

  public String getRedirectUri() {
    return redirectUri;
  }

  public static class SpotifyPlayerFactory extends PlayerFactory {

    private final Context context;
    private String clientId;

    public SpotifyPlayerFactory(Context context, String clientId) {
      this.context = context;
      this.clientId = clientId;
    }

    @Override
    public Player getPlayerFor(SkaldTrack track) {
      if (track instanceof SpotifyTrack) {

        return new SkaldSpotifyPlayer(context, clientId);
      }
      return null;
    }
  }
}
