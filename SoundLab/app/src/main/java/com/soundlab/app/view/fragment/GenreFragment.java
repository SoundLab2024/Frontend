package com.soundlab.app.view.fragment;

import static com.soundlab.app.utils.Constants.USER_TOKEN;
import static com.soundlab.app.utils.Utilities.showErrorMessage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundlab.R;
import com.soundlab.app.controller.ControllerCallback;
import com.soundlab.app.controller.SongController;
import com.soundlab.app.model.Song;
import com.soundlab.app.presenter.adapter.GenreAdapter;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;


public class GenreFragment extends Fragment {

    private SongController songController;
    private String token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        songController = new SongController();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_genre, container, false);

        Log.d("GenreFragment", "onCreateView called");

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        token = sharedPreferences.getString(USER_TOKEN, null);

        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.changeStatusBarColorFragment(this, R.color.alternative_purple);

        Bundle bundle = getArguments();

        if (bundle != null) {
            String genre = bundle.getString("genre");

            TextView genreTextView = view.findViewById(R.id.nomeGenre);
            genreTextView.setText(genre);

            retriveSongs(token, genre);
        }
        else {
            showErrorMessage(this, "Impossibile caricare il genere.");
        }
    }

    private void retriveSongs(String token, String genre) {
        songController.getSongsFromGenre(token, genre, new ControllerCallback<List<Song>>() {
            @Override
            public void onSuccess(List<Song> songs) {
                initAdapter(songs);
            }

            @Override
            public void onFailed(String errorMessage) {
                showErrorMessage(GenreFragment.this, errorMessage);
            }
        });
    }

    private void initAdapter(List<Song> songs) {

        View view = getView();

        if (view != null) {
            RecyclerView recyclerView = view.findViewById(R.id.songs_recyclerView);
            recyclerView.setNestedScrollingEnabled(false);

            GenreAdapter genreAdapter = new GenreAdapter(songs, this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);

            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(genreAdapter);
        }
    }


    public void loadPlayer(int songPosition, List<Song> songs) {
        Utilities.loadPlayer(requireActivity(), songPosition, (ArrayList<Song>) songs);
    }

    public void loadAddToPlaylistFragment(Song song) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("song", song);

        Fragment addToPlaylistFragment = new AddToPlaylistFragment();
        addToPlaylistFragment.setArguments(bundle);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideBottomNavigationView();
            ((MainActivity) getActivity()).replaceFragmentWithoutPopStack(addToPlaylistFragment, Utilities.addToPlaylistFragmentTag);
        }
    }
}