package agency.tango.skald.spotify;

import android.net.Uri;
import android.util.Log;

import agency.tango.skald.core.UriParser;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.spotify.models.SpotifyTrack;

public class SpotifyUriParser extends UriParser {
  private static final String TAG = SpotifyUriParser.class.getSimpleName();

  @Override
  public SkaldTrack parseSkaldTrack(SkaldTrack skaldTrack) {
    Uri uri = skaldTrack.getUri();
    String authority = uri.getAuthority();
    Log.d(TAG, authority);
    if (authority.equals("spotify")) {
      return new SpotifyTrack(skaldTrack.getUri());
    }
    return null;
  }
}
