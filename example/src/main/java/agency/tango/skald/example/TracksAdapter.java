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
import agency.tango.skald.R;
import agency.tango.skald.core.models.SkaldTrack;

public class TracksAdapter extends ArrayAdapter<SkaldTrack> {
  private final Context context;
  private final Graphics graphics;

  public TracksAdapter(@NonNull Context context, @LayoutRes int resource) {
    super(context, resource);
    this.context = context;
    this.graphics = new Graphics(context);
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
      graphics.draw(track.getImage(), imageView);
      textView.setText(String.format("%s - %s", track.getArtistName(), track.getTitle()));
    }
    return convertView;
  }
}
