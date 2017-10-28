package agency.tango.skald.example;

import android.content.Context;
import android.support.annotation.LayoutRes;
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
import agency.tango.skald.core.models.SkaldTrack;

public class TracksAdapter extends ArrayAdapter<SkaldTrack> {
  private final Context context;

  public TracksAdapter(@NonNull Context context, @LayoutRes int resource) {
    super(context, resource);
    this.context = context;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    SkaldTrack track = getItem(position);
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_layout, parent, false);
    }
    ImageView imageView = (ImageView) convertView.findViewById(R.id.image_list_cover);
    TextView textView = (TextView) convertView.findViewById(R.id.text_track_info);
    if (track != null) {
      if (!track.getImageUrl().isEmpty()) {
        Picasso
            .with(context)
            .load(track.getImageUrl())
            .into(imageView);
      }
      textView.setText(String.format("%s - %s", track.getArtistName(), track.getTitle()));
    }
    return convertView;
  }
}
