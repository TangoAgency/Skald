package agency.tango.skald.exoplayer.services;

import android.net.Uri;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import agency.tango.skald.core.SearchService;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.exoplayer.models.ExoPlayerTrack;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;

public class ExoPlayerDefaultSearchService implements SearchService {

  private List<String> directories;

  public ExoPlayerDefaultSearchService(String... directories) {
    this.directories = Arrays.asList(directories);
  }

  @Override
  public Single<List<SkaldTrack>> searchForTracks(final String query) {
    List<File> musicFiles = new ArrayList<>();

    for (String directory : directories) {
      File file = new File(directory);
      File[] matchingFiles = file.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return name.contains(query) && name.endsWith(".mp3");
        }
      });
      musicFiles.addAll(Arrays.asList(matchingFiles));
    }

    return Observable.fromIterable(musicFiles)
        .map(new Function<File, SkaldTrack>() {
          @Override
          public SkaldTrack apply(File file) throws Exception {
            return new ExoPlayerTrack(Uri.fromFile(file), "TESTOWY UTWÓR", "TESTOWY UTWÓR", "");
          }
        })
        .toList();
  }

  @Override
  public Single<List<SkaldPlaylist>> searchForPlaylists(String query) {
    return Single.just(Collections.<SkaldPlaylist>emptyList());
  }
}
