package agency.tango.skald.core;

import java.util.List;

import agency.tango.skald.core.models.SkaldCategory;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;

public interface SkaldMusicService {
  SkaldTrack getTrackInfo();

  List<SkaldPlaylist> getUserPlaylists();

  List<SkaldCategory> getCategories();

  List<SkaldPlaylist> getPlaylistsForCategory();

  List<SkaldTrack> getTracksForPlaylist();
}