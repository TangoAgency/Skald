package agency.tango.skald.deezer.services;

import com.deezer.sdk.model.User;
import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.request.DeezerRequest;
import com.deezer.sdk.network.request.DeezerRequestFactory;

import agency.tango.skald.core.UserService;
import agency.tango.skald.core.models.SkaldUser;
import agency.tango.skald.deezer.services.listeners.DeezerRequestListener;
import io.reactivex.Single;

public class DeezerUserService implements UserService {
  private static final String CURRENT_USER_REQUEST_ID = "current_user_request";
  private final DeezerConnect deezerConnect;

  public DeezerUserService(DeezerConnect deezerConnect) {
    this.deezerConnect = deezerConnect;
  }

  @Override
  public Single<SkaldUser> getUser() {
    return Single.create(emitter -> {
      final DeezerRequest deezerRequest = DeezerRequestFactory.requestCurrentUser();
      deezerRequest.setId(CURRENT_USER_REQUEST_ID);
      deezerConnect.requestAsync(deezerRequest,
          new DeezerRequestListener<SkaldUser>(emitter) {
            @Override
            public void onResult(Object result, Object requestId) {
              if (requestId.equals(CURRENT_USER_REQUEST_ID)) {
                SkaldUser skaldUser = mapDeezerUserToSkaldUser((User) result);
                emitter.onSuccess(skaldUser);
              }
            }
          });
    });
  }

  private SkaldUser mapDeezerUserToSkaldUser(User user) {
    return new SkaldUser(user.getFirstName(), user.getLastName(), user.getName(),
        user.getMediumImageUrl(), user.getEmail(), user.getCountry(),
        user.getBirthday().toString());
  }
}
