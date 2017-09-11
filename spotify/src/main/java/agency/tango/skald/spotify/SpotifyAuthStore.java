package agency.tango.skald.spotify;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import agency.tango.skald.core.AuthException;
import agency.tango.skald.core.Provider;
import agency.tango.skald.core.SkaldAuthData;
import agency.tango.skald.core.SkaldAuthStore;

public class SpotifyAuthStore implements SkaldAuthStore {
  private static final String SPOTIFY_JSON_KEY = "spotify_json";
  private static final String SPOTIFY_FILE_KEY = "agency.tango.skald.spotify.SPOTIFY_FILE_KEY";

  @Override
  public void save(SkaldAuthData skaldAuthData, Context context) {
    SharedPreferences sharedPreferences = getSharedPreferences(context);

    SpotifyAuthData spotifyAuthData = (SpotifyAuthData) skaldAuthData;

    Gson gson = new Gson();
    String json = gson.toJson(spotifyAuthData);

    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(SPOTIFY_JSON_KEY, json);
    editor.apply();
  }

  @Override
  public SkaldAuthData restore(Context context, Provider provider) throws AuthException {
    SharedPreferences sharedPreferences = getSharedPreferences(context);

    String json = sharedPreferences.getString(SPOTIFY_JSON_KEY, "");
    if(json.equals("")) {
      SpotifyProvider spotifyProvider = (SpotifyProvider) provider;
      throw new SpotifyAuthException("Cannot restore token", new SpotifyAuthError(context,
          spotifyProvider.getClientId(), spotifyProvider.getRedirectUri()));
    }

    Gson gson = new Gson();
    return gson.fromJson(json, SpotifyAuthData.class);
  }

  private SharedPreferences getSharedPreferences(Context context) {
    return context.getSharedPreferences(SPOTIFY_FILE_KEY, Context.MODE_PRIVATE);
  }
}
