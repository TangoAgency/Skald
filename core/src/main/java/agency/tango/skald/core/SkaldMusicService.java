package agency.tango.skald.core;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;

public class SkaldMusicService {
  private final List<Provider> providers = new ArrayList<>();
  private List<AuthorizationErrorListener> authorizationErrorListeners = new ArrayList<>();

  public SkaldMusicService(Context context, Provider... providers) {
    this.providers.addAll(Arrays.asList(providers));
  }

  void play(SkaldTrack skaldTrack) {
    for(AuthorizationErrorListener listener : authorizationErrorListeners) {
      listener.onAuthorizationError();
    }
  }

  void play(SkaldPlaylist skaldPlaylist) {
    for(AuthorizationErrorListener listener : authorizationErrorListeners)
      listener.onAuthorizationError();
  }

  void pause() {

  }

  void resume() {

  }

  void stop() {

  }

  void addAuthListener(AuthorizationErrorListener authorizationErrorListener) {
    authorizationErrorListeners.add(authorizationErrorListener);
  }

  void removeAuth(AuthorizationErrorListener authorizationErrorListener) {
    authorizationErrorListeners.remove(authorizationErrorListener);
  }

  List<SkaldTrack> searchTrack(String query) {
    return Collections.emptyList();
  }

  List<SkaldPlaylist> searchPlayList(String query) {
    return Collections.emptyList();
  }
}