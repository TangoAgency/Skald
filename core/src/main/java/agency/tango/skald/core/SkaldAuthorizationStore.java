package agency.tango.skald.core;

public interface SkaldAuthorizationStore {
  void save(SkaldAuthData session);

  boolean restore(SkaldAuthData session);
}
