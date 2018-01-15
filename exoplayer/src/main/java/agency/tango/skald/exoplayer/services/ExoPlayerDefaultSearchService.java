package agency.tango.skald.exoplayer.services;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.MimeTypeMap;
import com.google.android.exoplayer2.util.MimeTypes;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import agency.tango.skald.core.SearchService;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;
import agency.tango.skald.exoplayer.models.ExoPlayerImage;
import agency.tango.skald.exoplayer.models.ExoPlayerPlaylist;
import agency.tango.skald.exoplayer.models.ExoPlayerTrack;
import agency.tango.skald.exoplayer.models.parser.PlaylistM3uFileParser;
import agency.tango.skald.exoplayer.models.parser.PlaylistParser;
import agency.tango.skald.exoplayer.models.parser.PlaylistPlsFileParser;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ExoPlayerDefaultSearchService implements SearchService {
  private final List<PlaylistParser> playlistParsers;
  private final List<String> directories;
  private final Scheduler scheduler;

  public ExoPlayerDefaultSearchService(@NonNull List<String> directories) {
    this.directories = directories;
    this.playlistParsers = new ArrayList<>();
    fulfilParsersList();
    this.scheduler = Schedulers.io();
  }

  public ExoPlayerDefaultSearchService(@NonNull List<String> directories, Scheduler scheduler) {
    this.directories = directories;
    this.playlistParsers = new ArrayList<>();
    fulfilParsersList();
    this.scheduler = scheduler;
  }

  public ExoPlayerDefaultSearchService(@NonNull List<String> directories,
      @NonNull List<PlaylistParser> playlistParsers) {
    this.directories = directories;
    this.playlistParsers = playlistParsers;
    this.scheduler = Schedulers.io();
  }

  public ExoPlayerDefaultSearchService(@NonNull List<String> directories,
      @NonNull List<PlaylistParser> playlistParsers, Scheduler scheduler) {
    this.directories = directories;
    this.playlistParsers = playlistParsers;
    this.scheduler = scheduler;
  }

  @Override
  public Single<List<SkaldTrack>> searchForTracks(final String query) {
    return getAudioFiles(query)
        .map(new Function<File, SkaldTrack>() {
          @Override
          public SkaldTrack apply(File file) throws Exception {
            Tag tag = AudioFileIO.read(file).getTag();
            Artwork artwork = tag.getFirstArtwork();
            byte[] imageBinaryData = null;
            if (artwork != null) {
              imageBinaryData = artwork.getBinaryData();
            }
            return new ExoPlayerTrack(Uri.fromFile(file), tag.getFirst(FieldKey.ARTIST),
                tag.getFirst(FieldKey.TITLE), new ExoPlayerImage(imageBinaryData));
          }
        }).toList();
  }

  @Override
  public Single<List<SkaldPlaylist>> searchForPlaylists(final String query) {
    return getPlaylistFiles(query)
        .map(new Function<File, SkaldPlaylist>() {
          @Override
          public SkaldPlaylist apply(File file) throws Exception {
            return new ExoPlayerPlaylist(Uri.fromFile(file), getFileNameWithoutExtension(file),
                new ExoPlayerImage(null), getTracksUris(file));
          }
        })
        .toList();
  }

  private void fulfilParsersList() {
    this.playlistParsers.add(new PlaylistM3uFileParser());
    this.playlistParsers.add(new PlaylistPlsFileParser());
  }

  private Observable<File> getAudioFiles(final String query) {
    return getFiles(query, MimeTypes.AUDIO_MPEG, MimeTypes.AUDIO_MP4, MimeTypes.AUDIO_OPUS,
        MimeTypes.AUDIO_VORBIS);
  }

  private Observable<File> getPlaylistFiles(String query) {
    List<String> playlistsMimeTypes = new ArrayList<>();
    for (PlaylistParser playlistParser : playlistParsers) {
      playlistsMimeTypes.add(playlistParser.getMimeType());
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
    })
        .subscribeOn(scheduler);
  }

  private String getFileNameWithoutExtension(File file) {
    String extension = MimeTypeMap.getFileExtensionFromUrl(
        file.toURI().toString());
    int lastIndex = file.getName().indexOf(extension) - 1;
    return file.getName().substring(0, lastIndex);
  }

  private List<Uri> getTracksUris(File file) {
    String mimeType = getMimeType(file);
    for (PlaylistParser playlistParser : playlistParsers) {
      if (playlistParser.canRead(mimeType)) {
        return playlistParser.getTracksUris(Uri.fromFile(file));
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
