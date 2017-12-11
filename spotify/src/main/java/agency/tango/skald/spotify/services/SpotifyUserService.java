package agency.tango.skald.spotify.services;

import android.support.annotation.NonNull;
import agency.tango.skald.core.UserService;
import agency.tango.skald.core.models.SkaldUser;
import agency.tango.skald.spotify.api.SpotifyApi;
import agency.tango.skald.spotify.api.models.SpotifyUser;
import io.reactivex.Single;

public class SpotifyUserService implements UserService {
  private final SpotifyApi.SpotifyApiImpl spotifyApi;

  public SpotifyUserService(SpotifyApi.SpotifyApiImpl spotifyApi) {
    this.spotifyApi = spotifyApi;
  }

  @Override
  public Single<SkaldUser> getUser() {
    return spotifyApi.getSpotifyUser()
        .map(this::mapSpotifyUserToSkaldUser);
  }

  @NonNull
  private SkaldUser mapSpotifyUserToSkaldUser(SpotifyUser spotifyUser) {
    String imageUrl = null;
    if (spotifyUser.getImages().size() != 0) {
      imageUrl = spotifyUser.getImages().get(0).getUrl();
    }
    return new SkaldUser(spotifyUser.getDisplayName(), spotifyUser.getDisplayName(),
        spotifyUser.getId(), imageUrl, spotifyUser.getEmail(), spotifyUser.getCountry(),
        spotifyUser.getBirthdate());
  }
}
