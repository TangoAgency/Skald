package agency.tango.skald.core;

public interface SkaldAuthorizationStore {
  void save(SkaldAuthorizationData skaldAuthorizationData);

  SkaldAuthorizationData restore();
}
