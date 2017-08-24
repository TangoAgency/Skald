package agency.tango.spotify;

import android.app.Activity;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.List;

import agency.tango.core.MusicService;
import agency.tango.core.models.SkaldCategory;
import agency.tango.core.models.SkaldPlaylist;
import agency.tango.core.models.SkaldTrack;
import agency.tango.core.models.SkaldUser;

public class SpotifyMusicService implements MusicService {

  private final String clientID;
  private final String redirectUri;
  private final Activity contextActivity;
  private static final int REQUEST_CODE = 1337;

  public SpotifyMusicService(String clientID, String redirectUri, Activity contextActivity) {
    this.clientID = clientID;
    this.redirectUri = redirectUri;
    this.contextActivity = contextActivity;
  }

  @Override
  public void login() {
    final AuthenticationRequest request = new AuthenticationRequest.Builder(clientID,
        AuthenticationResponse.Type.TOKEN, redirectUri)
        .setScopes(new String[] {"user-read-private", "playlist-read-private",
            "playlist-read", "streaming"})
        .build();

    AuthenticationClient.openLoginActivity(contextActivity, REQUEST_CODE, request);
  }

  @Override
  public void logout() {

  }

  @Override
  public SkaldTrack getTrackInfo() {
    return null;
  }

  @Override
  public List<SkaldPlaylist> getUserPlaylists() {
    return null;
  }

  @Override
  public List<SkaldCategory> getCategories() {
    return null;
  }

  @Override
  public List<SkaldPlaylist> getPlaylistsForCategory() {
    return null;
  }

  @Override
  public List<SkaldTrack> getTracksForPlaylist() {
    return null;
  }

  @Override
  public SkaldUser getUser() {
    return null;
  }
}
