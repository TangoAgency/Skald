package agency.tango.spotify;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class SpotifyLoginButton extends LinearLayout {
  private final Context context;

  public SpotifyLoginButton(Context context) {
    this(context, null, 0);
  }

  public SpotifyLoginButton(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SpotifyLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.context = context;

    init();
  }

  private void init() {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(
        Context.LAYOUT_INFLATER_SERVICE);

    inflater.inflate(R.layout.view_spotify_login, this);
  }

  public void clickHandler() {
    Intent intent = new Intent(context, SpotifyLoginActivity.class);
    context.startActivity(intent);
  }
}
