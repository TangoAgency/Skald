package agency.tango.skald.spotify.services;

import android.content.Context;

import agency.tango.skald.core.UserService;
import agency.tango.skald.core.models.SkaldUser;
import agency.tango.skald.spotify.api.models.SpotifyUser;
import agency.tango.skald.spotify.api.models.Tokens;
import agency.tango.skald.spotify.authentication.SpotifyAuthData;
import agency.tango.skald.spotify.provider.SpotifyProvider;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;

public class SpotifyUserService extends SpotifyService implements UserService {

  public SpotifyUserService(Context context, SpotifyAuthData spotifyAuthData,
      SpotifyProvider spotifyProvider) {
    super(context, spotifyAuthData, spotifyProvider);
  }

  @Override
  public Single<SkaldUser> getUser() {
    return spotifyApi.getSpotifyUser()
        .onErrorResumeNext(new Function<Throwable, SingleSource<? extends SpotifyUser>>() {
          @Override
          public SingleSource<? extends SpotifyUser> apply(Throwable throwable) throws Exception {
            if (isTokenExpired(throwable)) {
              return refreshToken()
                  .flatMap(new Function<Tokens, SingleSource<SpotifyUser>>() {
                    @Override
                    public SingleSource<SpotifyUser> apply(Tokens tokens) throws Exception {
                      saveTokens(tokens);

                      return spotifyApi.getSpotifyUser();
                    }
                  });
            }

            return Single.just(new SpotifyUser());
          }
        })
        .map(new Function<SpotifyUser, SkaldUser>() {
          @Override
          public SkaldUser apply(SpotifyUser spotifyUser) throws Exception {
            String imageUrl = null;
            if (spotifyUser.getImages().size() != 0) {
              imageUrl = spotifyUser.getImages().get(0).getUrl();
            }
            return new SkaldUser(spotifyUser.getDisplayName(), spotifyUser.getDisplayName(),
                spotifyUser.getId(), imageUrl, spotifyUser.getEmail(), spotifyUser.getCountry(),
                spotifyUser.getBirthdate());
          }
        });
  }
}
