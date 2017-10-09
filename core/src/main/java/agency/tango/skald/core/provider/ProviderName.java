package agency.tango.skald.core.provider;

public abstract class ProviderName {
  private final String name;

  protected ProviderName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    ProviderName providerName = (ProviderName) object;
    return name != null ? name.equals(providerName.name) : providerName.name == null;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }
}
