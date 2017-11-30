package agency.tango.skald.spotify;

import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.spotify.provider.SpotifyProvider;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UriParsingTest {
  private SpotifyProvider spotifyProvider;

  @Before
  public void initialize() {
    spotifyProvider = new SpotifyProvider(InstrumentationRegistry.getTargetContext(), "clientID",
        "redirectUri", "clientSecret");
  }

  @Test
  public void parse_trackUriIsValid_shouldReturnTrue() {
    SkaldPlayableEntity skaldTrack = new SkaldTrack(Uri.parse("skald://spotify/track/spotify_uri"));

    assertTrue(spotifyProvider.canHandle(skaldTrack));
  }

  @Test
  public void parse_playlistUriIsValid_shouldReturnTrue() {
    SkaldPlayableEntity skaldPlaylist = new SkaldPlaylist(
        Uri.parse("skald://spotify/playlist/spotify_uri"));

    assertTrue(spotifyProvider.canHandle(skaldPlaylist));
  }

  @Test
  public void parse_uriSchemeIsNotValid_shouldReturnFalse() {
    SkaldPlayableEntity skaldTrack = new SkaldTrack(
        Uri.parse("skald1://spotify/track/spotify_uri"));

    assertFalse(spotifyProvider.canHandle(skaldTrack));
  }

  @Test
  public void parse_uriAuthorityIsNotValid_shouldReturnFalse() {
    SkaldPlayableEntity skaldTrack = new SkaldTrack(
        Uri.parse("skald://soptify/track/spotify_uri"));

    assertFalse(spotifyProvider.canHandle(skaldTrack));
  }

  @Test
  public void parse_trackUriPathIsNotValid_shouldReturnFalse() {
    SkaldPlayableEntity skaldTrack = new SkaldTrack(
        Uri.parse("skald://spotify/playlist/spotify_uri"));

    assertFalse(spotifyProvider.canHandle(skaldTrack));
  }

  @Test
  public void parse_playlistUriPathIsNotValid_shouldReturnFalse() {
    SkaldPlayableEntity skaldTrack = new SkaldPlaylist(
        Uri.parse("skald://spotify/track/spotify_uri"));

    assertFalse(spotifyProvider.canHandle(skaldTrack));
  }
}
