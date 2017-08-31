package agency.tango.skald.deezer;

import com.deezer.sdk.network.connect.DeezerConnect;

import agency.tango.skald.core.SkaldAuthorizationData;

public class DeezerAuthorizationData extends SkaldAuthorizationData {
  private final DeezerConnect deezerConnect;

  public DeezerAuthorizationData(DeezerConnect deezerConnect) {
    this.deezerConnect = deezerConnect;
  }
}
