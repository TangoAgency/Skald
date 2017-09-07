package agency.tango.skald.deezer;

import android.annotation.SuppressLint;
import android.os.Parcel;

import com.deezer.sdk.network.connect.DeezerConnect;

import agency.tango.skald.core.SkaldAuthData;

@SuppressLint("ParcelCreator")
public class DeezerAuthData extends SkaldAuthData {
  private final DeezerConnect deezerConnect;

  public DeezerAuthData(DeezerConnect deezerConnect) {
    this.deezerConnect = deezerConnect;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {

  }
}
