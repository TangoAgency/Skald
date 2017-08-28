package agency.tango.skald.core.models;

public class SkaldPlaylist {
  private final String uri;

  private final String name;

  public SkaldPlaylist(String uri, String name) {
    this.uri = uri;
    this.name = name;
  }

  public String getUri() {
    return uri;
  }

  public String getName() {
    return name;
  }
}