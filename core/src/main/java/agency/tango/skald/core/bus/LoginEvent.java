package agency.tango.skald.core.bus;

import agency.tango.skald.core.provider.ProviderName;

public class LoginEvent extends SkaldEvent {
  private final ProviderName providerName;

  public LoginEvent(ProviderName providerName) {
    this.providerName = providerName;
  }

  public ProviderName getProviderName() {
    return providerName;
  }
}
