package agency.tango.skald.core;

import agency.tango.skald.core.models.SkaldUser;
import io.reactivex.Single;

public interface UserService {
  Single<SkaldUser> getUser();
}
