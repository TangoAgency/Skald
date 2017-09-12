package agency.tango.skald.spotify;

import android.os.Parcel;
import android.os.Parcelable;

import agency.tango.skald.core.SkaldAuthData;

public class SpotifyAuthData extends SkaldAuthData {
  static final Parcelable.Creator<SpotifyAuthData> CREATOR =
      new Parcelable.Creator<SpotifyAuthData>() {
        @Override
        public SpotifyAuthData createFromParcel(Parcel in) {
          return new SpotifyAuthData(in);
        }

        @Override
        public SpotifyAuthData[] newArray(int size) {
          return new SpotifyAuthData[size];
        }
      };

  private final String oauthToken;
  private final String refreshToken;
  private final int expiresIn;

  SpotifyAuthData(String oauthToken, String refreshToken, int expiresIn) {
    this.oauthToken = oauthToken;
    this.refreshToken = refreshToken;
    this.expiresIn = expiresIn;
  }

  private SpotifyAuthData(Parcel in) {
    oauthToken = in.readString();
    refreshToken = in.readString();
    expiresIn = in.readInt();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(oauthToken);
    dest.writeString(refreshToken);
    dest.writeInt(expiresIn);
  }

  @Override
  public String toString() {
    return "SpotifyAuthData{" +
        "oauthToken='" + oauthToken + '\'' +
        ", refreshToken='" + refreshToken + '\'' +
        ", expiresIn=" + expiresIn +
        '}';
  }

  String getOauthToken() {
    return oauthToken;
  }

  String getRefreshToken() {
    return refreshToken;
  }

  int getExpiresIn() {
    return expiresIn;
  }
}
