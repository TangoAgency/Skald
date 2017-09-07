package agency.tango.skald.core.factories;

import agency.tango.skald.core.ApiCalls;
import agency.tango.skald.core.SkaldAuthData;

public abstract class ApiCallsFactory {
  public abstract ApiCalls getApiCalls(SkaldAuthData skaldAuthData);
}
