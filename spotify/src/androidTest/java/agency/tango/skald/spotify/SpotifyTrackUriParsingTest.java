package agency.tango.skald.spotify;

import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.spotify.provider.SpotifyProvider;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class SpotifyTrackUriParsingTest {
  private String uri;
  private Boolean isValid;
  private SpotifyProvider spotifyProvider;

  @Before
  public void initialize() {
    spotifyProvider = new SpotifyProvider(InstrumentationRegistry.getTargetContext(), "clientID",
        "redirectUri", "clientSecret");
  }

  public SpotifyTrackUriParsingTest(String uri, Boolean isValid) {
    this.uri = uri;
    this.isValid = isValid;
  }

  @Parameterized.Parameters
  public static Collection uris() {
    return Arrays.asList(new Object[][] {
        { "skald://spotify/track/spotify_uri", true },
        { "viking://spotify/track/spotify_uri", false },
        { "skald://soptify/track/spotify_uri", false },
        { "skald://spotify/playlist/spotify_uri", false }
    });
  }

  @Test
  public void testSpotifyTrackUris() {
    SkaldPlayableEntity skaldTrack = new SkaldTrack(Uri.parse(uri));

    assertEquals(spotifyProvider.canHandle(skaldTrack), isValid);
  }
}
