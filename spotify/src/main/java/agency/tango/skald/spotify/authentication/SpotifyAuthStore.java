package agency.tango.skald.spotify.authentication;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import agency.tango.skald.core.exceptions.AuthException;
import agency.tango.skald.core.authentication.SkaldAuthData;
import agency.tango.skald.core.authentication.SkaldAuthStore;
import agency.tango.skald.spotify.errors.SpotifyAuthError;
import agency.tango.skald.spotify.exceptions.SpotifyAuthException;
import agency.tango.skald.spotify.provider.SpotifyProvider;

public class SpotifyAuthStore implements SkaldAuthStore {
  private static final String SPOTIFY_JSON_KEY = "spotify_json";
  private static final String SPOTIFY_FILE_KEY = "agency.tango.skald.spotify.SPOTIFY_FILE_KEY";
  private static final String EMPTY = "";
  private final SpotifyProvider spotifyProvider;

  public SpotifyAuthStore(SpotifyProvider spotifyProvider) {
    this.spotifyProvider = spotifyProvider;
  }

  @Override
  public void save(Context context, SkaldAuthData skaldAuthData) {
    SharedPreferences sharedPreferences = getSharedPreferences(context);

    SpotifyAuthData spotifyAuthData = (SpotifyAuthData) skaldAuthData;

    Gson gson = new Gson();
    String json = gson.toJson(spotifyAuthData);

    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(SPOTIFY_JSON_KEY, json);
    editor.apply();
  }

  @Override
  public SkaldAuthData restore(Context context) throws AuthException {
    SharedPreferences sharedPreferences = getSharedPreferences(context);

    String json = sharedPreferences.getString(SPOTIFY_JSON_KEY, EMPTY);
    if(json.equals(EMPTY)) {
      throw new SpotifyAuthException("Cannot restore token", new SpotifyAuthError(context,
          spotifyProvider.getClientId(), spotifyProvider.getRedirectUri(),
          spotifyProvider.getClientSecret()));
    }

    Gson gson = new Gson();
    return gson.fromJson(json, SpotifyAuthData.class);
  }

  @Override
  public void clear(Context context) {
    SharedPreferences.Editor editor = getSharedPreferences(context).edit();
    editor.remove(SPOTIFY_JSON_KEY);
    editor.apply();
  }

  private SharedPreferences getSharedPreferences(Context context) {
    return context.getSharedPreferences(SPOTIFY_FILE_KEY, Context.MODE_PRIVATE);
  }
}
