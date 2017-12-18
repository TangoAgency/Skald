package agency.tango.skald.exoplayer.services;

import android.net.Uri;
import android.webkit.MimeTypeMap;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import agency.tango.skald.core.SearchService;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.exoplayer.models.ExoPlayerTrack;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;

public class ExoPlayerDefaultSearchService implements SearchService {

  private static final String AUDIO = "audio";

  private List<String> directories;

  public ExoPlayerDefaultSearchService(String... directories) {
    this.directories = Arrays.asList(directories);
  }

  @Override
  public Single<List<SkaldTrack>> searchForTracks(final String query) {
    return Observable.fromCallable(new Callable<List<File>>() {
      @Override
      public List<File> call() throws Exception {
        List<File> musicFiles = new ArrayList<>();

        for (String directory : directories) {
          File file = new File(directory);
          File[] matchingFiles = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
              String fileExtension = MimeTypeMap.getFileExtensionFromUrl(
                  file.toURI().toString());
              String mimeType = MimeTypeMap.getSingleton()
                  .getMimeTypeFromExtension(fileExtension.toLowerCase());

              return file.getName().contains(query) && mimeType != null &&
                  mimeType.contains(AUDIO);
            }
          });
          musicFiles.addAll(Arrays.asList(matchingFiles));
        }
        return musicFiles;
      }
    }).flatMapIterable(new Function<List<File>, Iterable<File>>() {
      @Override
      public Iterable<File> apply(List<File> files) throws Exception {
        return files;
      }
    }).map(new Function<File, SkaldTrack>() {
      @Override
      public SkaldTrack apply(File file) throws Exception {
        return new ExoPlayerTrack(Uri.fromFile(file), "TESTOWY UTWÓR", "TESTOWY UTWÓR", "");
      }
    }).toList();
  }

  @Override
  public Single<List<SkaldPlaylist>> searchForPlaylists(String query) {
    return Single.just(Collections.<SkaldPlaylist>emptyList());
  }
}
