package agency.tango.skald.core;

public interface Player {
  void play(String songUri);

  void stop();

  void pause();

  void resume();

  void release();
}
