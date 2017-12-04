package agency.tango.skald.deezer;

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
import agency.tango.skald.deezer.provider.DeezerProvider;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class DeezerTrackUriParsingTest {
  private String uri;
  private Boolean isValid;
  private DeezerProvider deezerProvider;

  @Before
  public void initialize() {
    deezerProvider = new DeezerProvider(InstrumentationRegistry.getTargetContext(), "clientID");
  }

  public DeezerTrackUriParsingTest(String uri, Boolean isValid) {
    this.uri = uri;
    this.isValid = isValid;
  }

  @Parameterized.Parameters
  public static Collection uris() {
    return Arrays.asList(new Object[][] {
        { "skald://deezer/track/deezer_uri", true },
        { "sklad://deezer/track/deezer_uri", false },
        { "skald://dezeer/track/deezer_uri", false },
        { "skald://deezer/playlist/deezer_uri", false }
    });
  }

  @Test
  public void testDeezerTrackUris() {
    SkaldPlayableEntity skaldTrack = new SkaldTrack(Uri.parse(uri));

    assertEquals(deezerProvider.canHandle(skaldTrack), isValid);
  }
}
