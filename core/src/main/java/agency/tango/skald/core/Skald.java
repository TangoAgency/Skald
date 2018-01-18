package agency.tango.skald.core;

import android.support.annotation.NonNull;
import java.util.Arrays;
import java.util.List;
import agency.tango.skald.core.provider.Provider;
import agency.tango.skald.core.provider.ProviderName;

public class Skald {
  private static volatile Skald instance;
  private final List<Provider> providers;

  private Skald(List<Provider> providers) {
    this.providers = CollectionsCompat.unmodifiableList(providers);
  }

  public static Skald instance() {
    if (instance == null) {
      throw new IllegalStateException("Must initialize Skald before using instance()");
    }
    return instance;
  }

  public static Skald with(@NonNull Provider... providers) {
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

  public static Skald with(@NonNull Skald skald) {
    if (instance == null) {
      synchronized (Skald.class) {
        if (instance == null) {
          setSkald(skald);
        }
      }
    }
    return instance;
  }

  @NonNull
  public List<Provider> providers() {
    return providers;
  }

  public Provider getProviderByName(ProviderName name) {
    for (Provider provider : providers) {
      if (provider.getProviderName().equals(name)) {
        return provider;
      }
    }
    throw new IllegalStateException("Cannot use not added or not supported provider");
  }

  private static void setSkald(Skald skald) {
    instance = skald;
  }

  public static class Builder {
    private Provider[] providers;

    public Skald.Builder providers(@NonNull Provider... providers) {
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
