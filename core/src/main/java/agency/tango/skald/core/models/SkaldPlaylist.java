package agency.tango.skald.core.models;

import android.net.Uri;
import android.support.annotation.NonNull;

public class SkaldPlaylist extends SkaldPlayableEntity {
  public static final String URI_PATH_FIRST_SEGMENT = "playlist";

  @NonNull
  private final String name;

  public SkaldPlaylist(Uri uri) {
    this(uri, "", "");
  }

  public SkaldPlaylist(@NonNull Uri uri, String imageUrl, @NonNull String name) {
    super(uri, imageUrl);
    this.name = name;
  }

  @NonNull
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}