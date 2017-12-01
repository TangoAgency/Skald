package agency.tango.skald.deezer;

import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.deezer.provider.DeezerProvider;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DeezerUriParsingTest {
  private DeezerProvider deezerProvider;

  @Before
  public void initialize() {
    deezerProvider = new DeezerProvider(InstrumentationRegistry.getTargetContext(), "clientID");
  }

  @Test
  public void parse_trackUriIsValid_shouldReturnTrue() {
    SkaldPlayableEntity skaldTrack = new SkaldTrack(Uri.parse("skald://deezer/track/spotify_uri"));

    assertTrue(deezerProvider.canHandle(skaldTrack));
  }

  @Test
  public void parse_playlistUriIsValid_shouldReturnTrue() {
    SkaldPlayableEntity skaldPlaylist = new SkaldPlaylist(
        Uri.parse("skald://deezer/playlist/spotify_uri"));

    assertTrue(deezerProvider.canHandle(skaldPlaylist));
  }

  @Test
  public void parse_uriSchemeIsNotValid_shouldReturnFalse() {
    SkaldPlayableEntity skaldTrack = new SkaldTrack(
        Uri.parse("skald321://deezer/track/spotify_uri"));
    SkaldPlayableEntity skaldPlaylist = new SkaldPlaylist(
        Uri.parse("sklad://deezer/track/spotify_uri"));

    assertFalse(deezerProvider.canHandle(skaldTrack));
    assertFalse(deezerProvider.canHandle(skaldPlaylist));
  }

  @Test
  public void parse_uriAuthorityIsNotValid_shouldReturnFalse() {
    SkaldPlayableEntity skaldTrack = new SkaldTrack(
        Uri.parse("skald://dezer/track/spotify_uri"));
    SkaldPlayableEntity skaldPlaylist = new SkaldPlaylist(
        Uri.parse("skald://dezeer/track/spotify_uri"));

    assertFalse(deezerProvider.canHandle(skaldTrack));
    assertFalse(deezerProvider.canHandle(skaldPlaylist));
  }

  @Test
  public void parse_trackUriPathIsNotValid_shouldReturnFalse() {
    SkaldPlayableEntity skaldTrack = new SkaldTrack(
        Uri.parse("skald://deezer/playlist/spotify_uri"));

    assertFalse(deezerProvider.canHandle(skaldTrack));
  }

  @Test
  public void parse_playlistUriPathIsNotValid_shouldReturnFalse() {
    SkaldPlayableEntity skaldTrack = new SkaldPlaylist(
        Uri.parse("skald://deezer/track/spotify_uri"));

    assertFalse(deezerProvider.canHandle(skaldTrack));
  }
}
