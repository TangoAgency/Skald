package agency.tango.core;

import java.util.List;

import agency.tango.core.models.SkaldCategory;
import agency.tango.core.models.SkaldPlaylist;
import agency.tango.core.models.SkaldTrack;
import agency.tango.core.models.SkaldUser;

public interface SkaldMusicService {

  SkaldTrack getTrackInfo();

  List<SkaldPlaylist> getUserPlaylists();

  List<SkaldCategory> getCategories();

  List<SkaldPlaylist> getPlaylistsForCategory();

  List<SkaldTrack> getTracksForPlaylist();

  SkaldUser getUser();
}