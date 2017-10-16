package agency.tango.skald.example;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import agency.tango.skald.R;
import agency.tango.skald.core.models.SkaldPlaylist;

public class PlaylistListFragment extends Fragment {
  private PlaylistAdapter playlistAdapter;
  private ListView listView;
  private Button searchButton;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_list, container, false);
    listView = (ListView) view.findViewById(R.id.list_view_tracks);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final SkaldPlaylist item = (SkaldPlaylist) parent.getItemAtPosition(position);
        ((MainActivity) getActivity()).play(item);
      }
    });

    searchButton = (Button) view.findViewById(R.id.button_refresh);
    searchButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        searchPlaylists();
      }
    });
    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    playlistAdapter = new PlaylistAdapter(getActivity().getApplicationContext(),
        R.layout.row_layout);
    listView.setAdapter(playlistAdapter);
  }

  public void searchPlaylists() {
    ((MainActivity) getActivity()).searchPlaylists(playlistAdapter);
  }
}
