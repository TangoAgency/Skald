package agency.tango.skald.core.models;

public class SkaldUser {
  private final String firstName;
  private final String lastName;
  private final String name;
  private final String imageUrl;
  private final String email;
  private final String country;
  private final String birthDate;

  public SkaldUser(String firstName, String lastName, String name, String imageUrl,
      String email, String country, String birthDate) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.name = name;
    this.imageUrl = imageUrl;
    this.email = email;
    this.country = country;
    this.birthDate = birthDate;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getName() {
    return name;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public String getEmail() {
    return email;
  }

  public String getCountry() {
    return country;
  }

  public String getBirthDate() {
    return birthDate;
  }
}
