package agency.tango.skald.spotify;

import android.content.Context;
import android.content.SharedPreferences;

import agency.tango.skald.core.SkaldAuthData;
import agency.tango.skald.core.SkaldAuthStore;

public class SpotifyAuthStore implements SkaldAuthStore {
  private static final String AUTH_TOKEN_KEY = "accessToken";
  private static final String EXPIRES_IN_KEY = "expiresIn";

  @Override
  public void save(SkaldAuthData skaldAuthData, Context context) {
    SharedPreferences sharedPreferences = getSharedPreferences(context);

    SpotifyAuthData spotifyAuthorizationData =
        (SpotifyAuthData) skaldAuthData;

    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(AUTH_TOKEN_KEY, spotifyAuthorizationData.getOauthToken());
    editor.putInt(EXPIRES_IN_KEY, spotifyAuthorizationData.getExpiresIn());
    editor.commit();
  }

  @Override
  public SkaldAuthData restore(Context context) {
    SharedPreferences sharedPreferences = getSharedPreferences(context);

    String authToken = sharedPreferences.getString(AUTH_TOKEN_KEY, "");
    int expiresIn = sharedPreferences.getInt(EXPIRES_IN_KEY, 0);

    if(authToken.equals("") && expiresIn == 0) {
      return null;
    }
    else {
      return new SpotifyAuthData(authToken, expiresIn);
    }
  }

  private SharedPreferences getSharedPreferences(Context context) {
    return context.getSharedPreferences(context.getString(R.string.preference_file_key),
        Context.MODE_PRIVATE);
  }
}
