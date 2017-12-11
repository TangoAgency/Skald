package agency.tango.skald.deezer.authentication;

import android.os.Parcel;
import android.os.Parcelable;
import com.deezer.sdk.network.connect.DeezerConnect;
import agency.tango.skald.core.authentication.SkaldAuthData;

public class DeezerAuthData extends SkaldAuthData {
  static final Parcelable.Creator<DeezerAuthData> CREATOR =
      new Parcelable.Creator<DeezerAuthData>() {
        @Override
        public DeezerAuthData createFromParcel(Parcel in) {
          return new DeezerAuthData(in);
        }

        @Override
        public DeezerAuthData[] newArray(int size) {
          return new DeezerAuthData[size];
        }
      };

  private final DeezerConnect deezerConnect;

  DeezerAuthData(DeezerConnect deezerConnect) {
    this.deezerConnect = deezerConnect;
  }

  private DeezerAuthData(Parcel in) {
    deezerConnect = (DeezerConnect) in.readValue(DeezerConnect.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(deezerConnect);
  }

  public DeezerConnect getDeezerConnect() {
    return deezerConnect;
  }
}
