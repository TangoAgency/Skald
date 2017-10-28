package agency.tango.skald.core.errors;

public class PlaybackError {
  private String message;

  public PlaybackError(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
