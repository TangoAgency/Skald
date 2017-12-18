package agency.tango.skald.exoplayer.services;

import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import agency.tango.skald.core.SearchService;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.exoplayer.models.ExoPlayerPlaylist;
import agency.tango.skald.exoplayer.models.ExoPlayerTrack;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;

public class ExoPlayerDefaultSearchService implements SearchService {
  private static final String AUDIO = "audio";
  private static final String URL = "url";

  private List<String> directories;

  public ExoPlayerDefaultSearchService(String... directories) {
    this.directories = Arrays.asList(directories);
  }

  @Override
  public Single<List<SkaldTrack>> searchForTracks(final String query) {
    return getFiles(query, true)
        .map(new Function<File, SkaldTrack>() {
          @Override
          public SkaldTrack apply(File file) throws Exception {
            return new ExoPlayerTrack(Uri.fromFile(file), "TESTOWY UTWÓR", "TESTOWY UTWÓR", "");
          }
        }).toList();
  }

  @Override
  public Single<List<SkaldPlaylist>> searchForPlaylists(final String query) {
    return getFiles(query, false)
        .map(new Function<File, SkaldPlaylist>() {
          @Override
          public SkaldPlaylist apply(File file) throws Exception {
            return new ExoPlayerPlaylist(Uri.fromFile(file), "TEST", "");
          }
        })
        .toList();
  }

  private Observable<File> getFiles(final String query, final boolean searchingForTracks) {
    return Observable.fromCallable(new Callable<List<File>>() {
      @Override
      public List<File> call() throws Exception {
        List<File> entitiesFiles = new ArrayList<>();

        for (String directory : directories) {
          File file = new File(directory);
          File[] matchingFiles = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
              String fileExtension = MimeTypeMap.getFileExtensionFromUrl(
                  file.toURI().toString());
              String mimeType = MimeTypeMap.getSingleton()
                  .getMimeTypeFromExtension(fileExtension.toLowerCase());

              Log.d("mimeType", mimeType);
              return file.getName().contains(query) && mimeType != null &&
                  mimeType.contains(AUDIO) && (searchingForTracks || mimeType.contains(URL));
            }
          });
          entitiesFiles.addAll(Arrays.asList(matchingFiles));
        }
        return entitiesFiles;
      }
    }).flatMapIterable(new Function<List<File>, Iterable<File>>() {
      @Override
      public Iterable<File> apply(List<File> files) throws Exception {
        return files;
      }
    });
  }
}
