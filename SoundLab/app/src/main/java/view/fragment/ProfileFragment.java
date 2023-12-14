package view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.soundlab.R;

import java.util.ArrayList;

import model.Playlist;
import presenter.adapter.PlaylistAdapter;

public class ProfileFragment extends Fragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Imposta il colore della barra di stato quando la vista è creata
        changeStatusBarColor(view, R.color.blue);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla il layout del fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Ottieni la RecyclerView dal layout
        RecyclerView recyclerView = view.findViewById(R.id.playlists_recyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        // Crea una nuova lista di playlist e aggiungi dati
        ArrayList<Playlist> playlistArrayList = new ArrayList<>();
        playlistArrayList.add(new Playlist("Soundlab", "Rock", R.drawable.playlist_default, false));
        playlistArrayList.add(new Playlist("test", "Rock", R.drawable.playlist_default, false));
        playlistArrayList.add(new Playlist("ok", "Rock", R.drawable.playlist_default, true));
        playlistArrayList.add(new Playlist("dio", "Rock", R.drawable.playlist_default, true));
        playlistArrayList.add(new Playlist("maiale", "Rock", R.drawable.playlist_default, false));
        playlistArrayList.add(new Playlist("gesu", "Rock", R.drawable.playlist_default, true));

        // Inizializza l'adapter e passa la lista di playlist
        PlaylistAdapter playlistAdapter = new PlaylistAdapter(requireContext(), playlistArrayList);

        // Imposta un layout manager per la RecyclerView (lista verticale)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);

        // Imposta il layout manager e l'adapter per la RecyclerView
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(playlistAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Cambia il colore della barra di stato quando la vista viene distrutta
        if (getView() != null) {
            changeStatusBarColor(getView(), R.color.dark_purple);
        }
    }

    // Metodo per cambiare il colore della barra di stato
    private void changeStatusBarColor(View view, int color) {
        int statusBarColor = ContextCompat.getColor(view.getContext(), color);

        // Imposta il colore della barra di stato
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().setStatusBarColor(statusBarColor);
        }
    }
}