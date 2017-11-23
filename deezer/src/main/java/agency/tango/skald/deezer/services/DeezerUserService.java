package agency.tango.skald.deezer.services;

import agency.tango.skald.core.UserService;
import agency.tango.skald.core.models.SkaldUser;
import io.reactivex.Single;

public class DeezerUserService implements UserService {
  @Override
  public Single<SkaldUser> getUser() {
    return Single.just(new SkaldUser("", "", "","","","",""));
  }
}
