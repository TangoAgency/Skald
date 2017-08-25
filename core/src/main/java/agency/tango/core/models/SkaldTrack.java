package agency.tango.core.models;

public class SkaldTrack {
  private String uri;
  private String name;

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
