package agency.tango.skald.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import agency.tango.skald.core.provider.Provider;
import agency.tango.skald.core.provider.ProviderName;

public class Skald {
  private static volatile Skald singleton;
  private static final List<Provider> providers = new ArrayList<>();

  private Skald() {
  }

  public static Skald singleton() {
    if (singleton == null) {
      return new Skald();
    }
    return singleton;
  }

  public static void with(Provider... providers) {
    Skald.providers.addAll(Arrays.asList(providers));
  }

  public List<Provider> providers() {
    return providers;
  }

  public Provider getProviderByName(ProviderName name) {
    for (Provider provider : providers) {
      if (provider.getProviderName().equals(name)) {
        return provider;
      }
    }
    return null;
  }
}
