package agency.tango.skald.example;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import agency.tango.skald.core.models.SkaldImage;
import agency.tango.skald.deezer.models.DeezerImage;
import agency.tango.skald.exoplayer.models.ExoPlayerImage;
import agency.tango.skald.spotify.models.SpotifyImage;

public class Graphics {
  private final Context context;

  public Graphics(Context context) {
    this.context = context;
  }

  public void draw(SkaldImage skaldImage, ImageView imageView) {
    if (skaldImage instanceof SpotifyImage) {
      drawAnImageWithPicasso(((SpotifyImage) skaldImage).getImageUrl(), imageView);
    } else if (skaldImage instanceof DeezerImage) {
      drawAnImageWithPicasso(((DeezerImage) skaldImage).getImageUrl(), imageView);
    } else if ((skaldImage instanceof ExoPlayerImage)) {
      byte[] pictureData = ((ExoPlayerImage) skaldImage).getPictureData();
      Bitmap bmp = null;
      if (pictureData != null) {
        bmp = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
      }

      imageView.setImageBitmap(bmp);
    }
  }

  private void drawAnImageWithPicasso(String imageUrl, ImageView imageView) {
    if (imageUrl != null && !imageUrl.isEmpty()) {
      Picasso
          .with(context)
          .load(imageUrl)
          .into(imageView);
    }
  }
}
