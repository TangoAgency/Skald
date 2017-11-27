package agency.tango.skald.spotify.exceptions;

public class TokenRefreshException extends Exception{
  public TokenRefreshException(String message, Throwable cause) {
    super(message, cause);
  }
}
