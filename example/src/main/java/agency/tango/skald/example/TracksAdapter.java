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

import java.util.Collection;
import java.util.List;

import agency.tango.skald.R;
import agency.tango.skald.core.models.SkaldTrack;

public class TracksAdapter extends ArrayAdapter<SkaldTrack> {
  private final Context context;
  private List<SkaldTrack> tracks;

  public TracksAdapter(@NonNull Context context,
      @LayoutRes int resource, List<SkaldTrack> tracks) {
    super(context, resource, tracks);
    this.context = context;
    this.tracks = tracks;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    SkaldTrack track = tracks.get(position);
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_layout, parent, false);
    }
    ImageView imageView = (ImageView) convertView.findViewById(R.id.image_list_cover);
    TextView textView = (TextView) convertView.findViewById(R.id.text_track_info);
    Picasso
        .with(context)
        .load(track.getImageUrl())
        .into(imageView);
    textView.setText(String.format("%s - %s", track.getArtistName(), track.getTitle()));

    return convertView;
  }

  @Override
  public void addAll(@NonNull Collection<? extends SkaldTrack> collection) {
    super.addAll(collection);
  }
}
