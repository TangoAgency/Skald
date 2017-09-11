package agency.tango.skald.spotify.api.models;

import com.google.gson.annotations.SerializedName;

public class Tokens {
  @SerializedName("access_token")
  private String accessToken;

  @SerializedName("token_type")
  private String tokenType;

  @SerializedName("scope")
  private String scope;

  @SerializedName("expires_in")
  private Integer expiresIn;

  @SerializedName("refresh_token")
  private String refreshToken;

  public String getAccessToken() {
    return accessToken;
  }

  public String getTokenType() {
    return tokenType;
  }

  public String getScope() {
    return scope;
  }

  public Integer getExpiresIn() {
    return expiresIn;
  }

  public String getRefreshToken() {
    return refreshToken;
  }
}
