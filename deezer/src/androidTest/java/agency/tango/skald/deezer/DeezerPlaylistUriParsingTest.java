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
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.deezer.provider.DeezerProvider;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class DeezerPlaylistUriParsingTest {
  private String uri;
  private Boolean isValid;
  private DeezerProvider deezerProvider;

  @Before
  public void initialize() {
    deezerProvider = new DeezerProvider(InstrumentationRegistry.getTargetContext(), "clientID");
  }

  public DeezerPlaylistUriParsingTest(String uri, Boolean isValid) {
    this.uri = uri;
    this.isValid = isValid;
  }

  @Parameterized.Parameters
  public static Collection uris() {
    return Arrays.asList(new Object[][] {
        {"skald://deezer/playlist/deezer_uri", true},
        {"sklad://deezer/track/deezer_uri", false},
        {"skald://dezeer/track/deezer_uri", false},
        {"skald://deezer/track/deezer_uri", false}
    });
  }

  @Test
  public void testDeezerPlaylistUris() {
    SkaldPlayableEntity skaldPlaylist = new SkaldPlaylist(Uri.parse(uri));

    assertEquals(deezerProvider.canHandle(skaldPlaylist), isValid);
  }
}
