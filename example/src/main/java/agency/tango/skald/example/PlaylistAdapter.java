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
import agency.tango.skald.core.models.SkaldPlaylist;

public class PlaylistAdapter extends ArrayAdapter<SkaldPlaylist> {
  private final Context context;

  public PlaylistAdapter(@NonNull Context context, @LayoutRes int resource) {
    super(context, resource);
    this.context = context;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    SkaldPlaylist playlist = getItem(position);
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_layout, parent, false);
    }
    ImageView imageView = convertView.findViewById(R.id.image_list_cover);
    TextView textView = convertView.findViewById(R.id.text_track_info);
    if (playlist != null) {
      if (!playlist.getImageUrl().isEmpty()) {
        Picasso
            .with(context)
            .load(playlist.getImageUrl())
            .into(imageView);
      }
      textView.setText(playlist.getName());
    }
    return convertView;
  }
}
