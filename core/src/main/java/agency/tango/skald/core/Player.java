package agency.tango.skald.core;

import agency.tango.skald.core.models.SkaldTrack;

public interface Player {
  void play(SkaldTrack track);

  void stop();

  void pause();

  void resume();

  void release();
}
