package agency.tango.skald.core;

import java.util.Arrays;
import java.util.List;
import agency.tango.skald.core.provider.Provider;
import agency.tango.skald.core.provider.ProviderName;

public class Skald {
  private static volatile Skald instance;
  private final List<Provider> providers;

  private Skald(List<Provider> providers) {
    this.providers = providers;
  }

  public static Skald instance() {
    if (instance == null) {
      throw new IllegalStateException("Must initialize Skald before using instance()");
    }
    return instance;
  }

  public static Skald with(Provider... providers) {
    if (instance == null) {
      synchronized (Skald.class) {
        if (instance == null) {
          setSkald(new Skald.Builder()
              .providers(providers)
              .build());
        }
      }
    }
    return instance;
  }

  public static Skald with(Skald skald) {
    if (instance == null) {
      synchronized (Skald.class) {
        if (instance == null) {
          setSkald(skald);
        }
      }
    }
    return instance;
  }

  // TODO return unmodifable / immutable list
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

  private static void setSkald(Skald skald) {
    instance = skald;
  }

  public static class Builder {
    private Provider[] providers;

    public Skald.Builder providers(Provider... providers) {
      if (this.providers != null) {
        throw new IllegalArgumentException("Providers already set");
      } else {
        this.providers = providers;
        return this;
      }
    }

    public Skald build() {
      return new Skald(Arrays.asList(this.providers));
    }
  }
}
