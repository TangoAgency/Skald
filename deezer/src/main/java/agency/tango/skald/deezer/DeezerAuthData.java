package agency.tango.skald.deezer;

import com.deezer.sdk.network.connect.DeezerConnect;

import agency.tango.skald.core.SkaldAuthData;

public class DeezerAuthData extends SkaldAuthData {
  private final DeezerConnect deezerConnect;

  public DeezerAuthData(DeezerConnect deezerConnect) {
    this.deezerConnect = deezerConnect;
  }
}
