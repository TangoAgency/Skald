package agency.tango.skald.core.errors;

public class PlaybackError {
  private Exception exception;

  public PlaybackError(Exception exception) {
    this.exception = exception;
  }

  public Exception getException() {
    return exception;
  }
}
