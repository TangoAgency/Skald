package agency.tango.skald.core.models;

public class SkaldTrack {
  private final String uri;
  private final String name;

  public SkaldTrack(String uri, String name) {
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
