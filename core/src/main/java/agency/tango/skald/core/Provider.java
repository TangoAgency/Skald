package agency.tango.skald.core;

public abstract class Provider {
  public abstract String getProviderName();
  public abstract PlayerFactory getPlayerFactory();
  public abstract SkaldAuthStoreFactory getSkaldAuthStoreFactory();
  public abstract AuthErrorFactory getAuthErrorFactory();
}
