package agency.tango.skald.example;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import agency.tango.skald.R;
import agency.tango.skald.core.models.SkaldPlayableEntity;
import agency.tango.skald.core.models.SkaldPlaylist;
import agency.tango.skald.core.models.SkaldTrack;

public class SkaldEntityAdapter extends ArrayAdapter<SkaldPlayableEntity> {
  private final Context context;

  public SkaldEntityAdapter(@NonNull Context context) {
    super(context, R.layout.row_layout);
    this.context = context;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    SkaldPlayableEntity entity = getItem(position);
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_layout, parent, false);
    }
    ImageView coverImageView = convertView.findViewById(R.id.image_list_cover);
    TextView trackInfoTextView = convertView.findViewById(R.id.text_track_info);
    if (entity != null) {
      if (!entity.getImageUrl().isEmpty()) {
        Picasso
            .with(context)
            .load(entity.getImageUrl())
            .into(coverImageView);
      }

      trackInfoTextView.setText(getTrackInfo(entity));
    }
    return convertView;
  }

  private String getTrackInfo(SkaldPlayableEntity entity) {
    if (entity instanceof SkaldTrack) {
      SkaldTrack track = (SkaldTrack) entity;
      return String.format("%s - %s", track.getArtistName(), track.getTitle());
    } else {
      SkaldPlaylist playlist = (SkaldPlaylist) entity;
      return playlist.getName();
    }
  }
}
