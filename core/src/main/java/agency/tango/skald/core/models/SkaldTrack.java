package agency.tango.skald.core.models;

import android.net.Uri;
import android.support.annotation.NonNull;

public class SkaldTrack extends SkaldPlayableEntity {
  public static final String URI_PATH_FIRST_SEGMENT = "track";

  @NonNull
  private final String artistName;

  @NonNull
  private final String title;

  public SkaldTrack(Uri uri) {
    this(uri, "", "", "");
  }

  public SkaldTrack(@NonNull Uri uri, String imageUrl, @NonNull String artistName,
      @NonNull String title) {
    super(uri, imageUrl);
    this.artistName = artistName;
    this.title = title;
  }

  @NonNull
  public String getArtistName() {
    return artistName;
  }

  @NonNull
  public String getTitle() {
    return title;
  }

  @Override
  public String toString() {
    return String.format("%s - %s", artistName, title);
  }
}
