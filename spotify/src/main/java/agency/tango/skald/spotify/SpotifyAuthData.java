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
  private final int expiresIn;

  public SpotifyAuthData(String oauthToken, int expiresIn) {
    this.oauthToken = oauthToken;
    this.expiresIn = expiresIn;
  }

  private SpotifyAuthData(Parcel in) {
    oauthToken = in.readString();
    expiresIn = in.readInt();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(oauthToken);
    dest.writeInt(expiresIn);
  }

  @Override
  public String toString() {
    return "SpotifyAuthData{" +
        "oauthToken='" + oauthToken + '\'' +
        ", expiresIn=" + expiresIn +
        '}';
  }

  public String getOauthToken() {
    return oauthToken;
  }

  public int getExpiresIn() {
    return expiresIn;
  }
}
