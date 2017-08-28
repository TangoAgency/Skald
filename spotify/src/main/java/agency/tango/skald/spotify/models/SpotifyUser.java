package agency.tango.skald.spotify.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpotifyUser {
  @SerializedName("birthdate")
  private String birthdate;

  @SerializedName("country")
  private String country;

  @SerializedName("display_name")
  private String displayName;

  @SerializedName("email")
  private String email;

  @SerializedName("followers")
  private Followers followers;

  @SerializedName("href")
  private String href;

  @SerializedName("id")
  private String id;

  @SerializedName("images")
  private List<Image> images;

  @SerializedName("product")
  private String product;

  @SerializedName("type")
  private String type;

  @SerializedName("uri")
  private String uri;

  public String getBirthdate() {
    return birthdate;
  }

  public String getCountry() {
    return country;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getEmail() {
    return email;
  }

  public Followers getFollowers() {
    return followers;
  }

  public String getHref() {
    return href;
  }

  public String getId() {
    return id;
  }

  public List<Image> getImages() {
    return images;
  }

  public String getProduct() {
    return product;
  }

  public String getType() {
    return type;
  }

  public String getUri() {
    return uri;
  }

  @Override
  public String toString() {
    return "SpotifyUser{" +
        "birthdate='" + birthdate + '\'' +
        ", country='" + country + '\'' +
        ", displayName='" + displayName + '\'' +
        ", email='" + email + '\'' +
        ", followers=" + followers +
        ", href='" + href + '\'' +
        ", id='" + id + '\'' +
        ", images=" + images +
        ", product='" + product + '\'' +
        ", type='" + type + '\'' +
        ", uri='" + uri + '\'' +
        '}';
  }
}