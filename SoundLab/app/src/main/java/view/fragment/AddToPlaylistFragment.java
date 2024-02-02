package view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.soundlab.R;

import model.Playlist;
import model.Song;

public class AddToPlaylistFragment extends Fragment {

    private Song song;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_to_playlist, container, false);

        Log.d("AddToPlaylistFragment", "onCreateView called");

        Bundle bundle = getArguments();

        if (bundle != null) {
            song = (Song) bundle.getSerializable("song");

            if (song != null) {
                Log.d("AddToPlaylistFragment", song.getName());
            }
        }


        return view;
    }


}
