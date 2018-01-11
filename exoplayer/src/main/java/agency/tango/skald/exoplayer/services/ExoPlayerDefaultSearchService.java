package agency.tango.skald.exoplayer.services;

import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import com.google.android.exoplayer2.util.MimeTypes;
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
import agency.tango.skald.exoplayer.models.reader.PlaylistM3uFileReader;
import agency.tango.skald.exoplayer.models.reader.PlaylistReader;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;

public class ExoPlayerDefaultSearchService implements SearchService {
  private static final String AUDIO = "audio";
  private static final String URL = "url";

  private static final String PLAYLIST_PLS = "audio/x-scpls";

  private final List<PlaylistReader> playlistReaders = new ArrayList<>();
  private final List<String> directories;

  public ExoPlayerDefaultSearchService(String... directories) {
    this.directories = Arrays.asList(directories);
    this.playlistReaders.add(new PlaylistM3uFileReader());
    //TODO implement pls format reader
  }

  public ExoPlayerDefaultSearchService(PlaylistReader playlistReader, String... directories) {
    this(directories);
    this.playlistReaders.add(playlistReader);
  }

  @Override
  public Single<List<SkaldTrack>> searchForTracks(final String query) {
    return getAudioFiles(query)
        .map(new Function<File, SkaldTrack>() {
          @Override
          public SkaldTrack apply(File file) throws Exception {
            return new ExoPlayerTrack(Uri.fromFile(file), "TESTOWY UTWÓR", "TESTOWY UTWÓR", "");
          }
        }).toList();
  }

  @Override
  public Single<List<SkaldPlaylist>> searchForPlaylists(final String query) {
    return getPlaylistFiles(query)
        .map(new Function<File, SkaldPlaylist>() {
          @Override
          public SkaldPlaylist apply(File file) throws Exception {
            List<ExoPlayerTrack> tracks = getTracks(file);
            return new ExoPlayerPlaylist(Uri.fromFile(file), "TEST", "", tracks);
          }
        })
        .toList();
  }

  private Observable<File> getAudioFiles(final String query) {
    return getFiles(query, MimeTypes.AUDIO_MPEG);
  }

  private Observable<File> getPlaylistFiles(String query) {
    List<String> playlistsMimeTypes = new ArrayList<>();
    for (PlaylistReader playlistReader : playlistReaders) {
      playlistsMimeTypes.add(playlistReader.getMimeType());
    }
    return getFiles(query, playlistsMimeTypes.toArray(new String[playlistsMimeTypes.size()]));
  }

  private Observable<File> getFiles(final String query, final String... mimeTypes) {
    return Observable.fromCallable(new Callable<List<File>>() {
      @Override
      public List<File> call() throws Exception {
        List<File> entitiesFiles = new ArrayList<>();

        for (String directory : directories) {
          File file = new File(directory);
          File[] matchingFiles = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
              String mimeType = getMimeType(file);

              Log.d("mimeType", mimeType);
              return file.getName().contains(query) && checkMimeTypes(mimeType, mimeTypes);
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

  private List<ExoPlayerTrack> getTracks(File file) {
    String mimeType = getMimeType(file);
    for (PlaylistReader playlistReader : playlistReaders) {
      if (playlistReader.canRead(mimeType)) {
        return playlistReader.getTracks(Uri.fromFile(file));
      }
    }
    return new ArrayList<>();
  }

  private boolean checkMimeTypes(String mimeType, String... mimeTypes) {
    for (String supportedMimeType : mimeTypes) {
      if (supportedMimeType.equals(mimeType)) {
        return true;
      }
    }
    return false;
  }

  private String getMimeType(File file) {
    String fileExtension = MimeTypeMap.getFileExtensionFromUrl(
        file.toURI().toString());
    return MimeTypeMap.getSingleton()
        .getMimeTypeFromExtension(fileExtension.toLowerCase());
  }
}
