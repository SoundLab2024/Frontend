package com.soundlab.app.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.soundlab.R;
import com.soundlab.app.model.Artist;
import com.soundlab.app.model.Listening;
import com.soundlab.app.view.CustomButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AnaliticheFragment extends Fragment {

    private List<Listening> listeningList;
    private List<Listening> songStatsList;
    private EditText searchInput;
    private boolean searchByUser = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initializeLists();

        View view = inflater.inflate(R.layout.fragment_analitiche, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeLists() {
        // Lista per memorizzare gli ascolti di un utente
        listeningList = new ArrayList<>();
        listeningList.add(new Listening("Utente1", null, null, null, 10, "Mattina"));
        listeningList.add(new Listening("Utente2", null, null, null, 11, "Pomeriggio"));
        listeningList.add(new Listening("Utente3", null, null, null, 12, "Notte"));

        // Lista per memorizzare le informazioni sugli artisti
        List<Artist> singers = new ArrayList<>();
        singers.add(new Artist(1, "Artista1", new Date(2002, 12, 12), "Italiano"));
        singers.add(new Artist(2, "Artista2", new Date(2002, 12, 12), "Italiano"));

        // Lista per memorizzare le statistiche di una canzone
        songStatsList = new ArrayList<>();
        songStatsList.add(new Listening(null, "Canzone1", "Original", Arrays.asList(singers.get(0)), 11, null));
        songStatsList.add(new Listening(null, "Canzone2", "Original", null, 10, null));
        songStatsList.add(new Listening(null, "Canzone3", "Original", null, 20, null));
    }


    /**
     * Inizializza le View all'interno del layout del frammento.
     *
     * @param view La View radice del layout del frammento.
     */
    private void initializeViews(View view) {
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        RadioButton userRadioButton = view.findViewById(R.id.user_radio_button);
        RadioButton songRadioButton = view.findViewById(R.id.song_radio_button);

        searchInput = view.findViewById(R.id.search);
        CustomButton searchButton = view.findViewById(R.id.searchButton);

        // Imposta un listener per il click sul pulsante di ricerca
        searchButton.setOnClickListener(v -> performSearch(searchInput.getText().toString()));

        // Imposta un listener per il cambio di selezione nel RadioGroup
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            searchByUser = checkedId == userRadioButton.getId();
        });
    }


    /**
     * Esegue una ricerca in base alla query specificata.
     *
     * @param query La stringa di ricerca inserita dall'utente.
     */
    private void performSearch(String query) {
        // Verifica se la query è vuota
        if (query.isEmpty()) {
            showToast("Inserisci elementi validi per la ricerca");
            return;
        }

        // Verifica se la ricerca è basata sull'utente e la query non corrisponde agli ascolti
        if (searchByUser && !searchInListeningList(query)) {
            showToast("Errore: La ricerca non corrisponde al tipo selezionato");
            return;
        }

        // Verifica se la ricerca è basata sulla canzone e la query non corrisponde alle statistiche delle canzoni
        if (!searchByUser && !searchInSongStatsList(query)) {
            showToast("Errore: La ricerca non corrisponde al tipo selezionato");
            return;
        }

        // Esegue la ricerca in base al tipo selezionato e la mostro
        List<Listening> searchResults = searchByUser ? searchUser(query) : searchSong(query);
        displaySearchResults(searchResults);
    }


    /**
     * Cerca la query specificata nella lista degli ascolti degli utenti.
     *
     * @param query La stringa di ricerca da cercare.
     * @return True se la query corrisponde a un utente nella lista degli ascolti, altrimenti False.
     */
    private boolean searchInListeningList(String query) {
        for (Listening user : listeningList) {
            // Controlla se l'utente corrente non è nullo e se il suo nome contiene la query
            // No sensitive-case
            if (user.getUtente() != null && user.getUtente().toLowerCase().contains(query.toLowerCase())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Cerca la query specificata nella lista delle statistiche delle canzoni.
     *
     * @param query La stringa di ricerca da cercare.
     * @return True se la query corrisponde al nome di una canzone nella lista delle statistiche delle canzoni, altrimenti False.
     */
    private boolean searchInSongStatsList(String query) {
        for (Listening song : songStatsList) {
            // Controlla se il nome della canzone corrente non è nullo e se contiene la query
            if (song.getCanzone() != null && song.getCanzone().toLowerCase().contains(query.toLowerCase())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Esegue una ricerca degli ascolti corrispondenti all'utente specificato dalla query.
     *
     * @param query La stringa di ricerca dell'utente.
     * @return Una lista contenente gli ascolti corrispondenti all'utente specificato.
     */
    private List<Listening> searchUser(String query) {
        // Lista per memorizzare i risultati della ricerca
        List<Listening> searchResults = new ArrayList<>();
        for (Listening user : listeningList) {
            // Controlla se l'utente corrente non è nullo e se il suo nome contiene la query
            if (user.getUtente() != null && user.getUtente().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(user);
            }
        }
        return searchResults;
    }


    /**
     * Esegue una ricerca delle statistiche delle canzoni corrispondenti al nome specificato dalla query.
     *
     * @param query La stringa di ricerca del nome della canzone.
     * @return Una lista contenente le statistiche delle canzoni corrispondenti al nome specificato.
     */
    private List<Listening> searchSong(String query) {
        List<Listening> searchResults = new ArrayList<>();
        for (Listening song : songStatsList) {
            // Controlla se il nome della canzone corrente non è nullo e se contiene la query
            if (song.getCanzone() != null && song.getCanzone().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(song);
            }
        }
        return searchResults;
    }


    /**
     * Visualizza i risultati della ricerca nella tabella dell'interfaccia utente.
     *
     * @param searchResults La lista dei risultati della ricerca da visualizzare.
     */
    private void displaySearchResults(List<Listening> searchResults) {
        TableLayout tableLayout = getView().findViewById(R.id.table);
        tableLayout.removeAllViews();

        // Determina le colonne da visualizzare in base al tipo di ricerca (utente o canzone)
        List<String> columns = searchByUser ? Arrays.asList("ㅤ", "Utente", "ㅤ", "Numero Ascolti", "Fascia Oraria") :
                Arrays.asList("ㅤ", "Canzone", "Tipo", "Artista", "Numero Ascolti");

        TableRow headerRow = new TableRow(getContext());
        for (String columnName : columns) {
            TextView headerTextView = new TextView(getContext());
            headerTextView.setText(columnName);
            headerRow.addView(headerTextView);
        }
        tableLayout.addView(headerRow);

        for (int i = 0; i < searchResults.size(); i++) {
            Listening listening = searchResults.get(i);
            TableRow row = new TableRow(getContext());
            TextView numberTextView = new TextView(getContext());
            numberTextView.setText(String.valueOf(i + 1));
            row.addView(numberTextView);
            addCellToRow(row, searchByUser ? listening.getUtente() : listening.getCanzone());
            addCellToRow(row, listening.getSongType());
            if (!searchByUser) {
                // Se la ricerca è basata sulla canzone, aggiunge il nome dell'artista per quella canzone
                String artistName = getArtistNameForSong(listening.getCanzone());
                addCellToRow(row, artistName != null ? artistName : " "); // Se non ci sono informazioni sull'artista, aggiunge uno spazio vuoto
            }

            // Aggiunge il numero totale di ascolti e la fascia oraria alla riga corrente
            addCellToRow(row, String.valueOf(listening.getTotalListens()));
            addCellToRow(row, listening.getTimeSlot());
            tableLayout.addView(row);
        }
    }


    /**
     * Ottiene il nome dell'artista per una determinata canzone.
     *
     * @param songTitle Il titolo della canzone di cui ottenere l'artista.
     * @return Il nome dell'artista per la canzone specificata, se presente, altrimenti null.
     */
    private String getArtistNameForSong(String songTitle) {
        for (Listening song : songStatsList) {

            // Controlla se il nome della canzone corrente non è nullo e se corrisponde al titolo della canzone specificato
            if (song.getCanzone() != null && song.getCanzone().equalsIgnoreCase(songTitle)) {
                List<Artist> songArtists = song.getSingers(); // Ottiene gli artisti associati alla canzone
                // Se sono presenti artisti associati alla canzone, restituisce il nome del primo artista
                if (songArtists != null && !songArtists.isEmpty()) {
                    return songArtists.get(0).getName();
                }
            }
        }
        return null;
    }


    /**
     * Aggiunge una cella di testo a una riga della tabella.
     *
     * @param row  La riga a cui aggiungere la cella di testo.
     * @param text Il testo da visualizzare nella cella.
     */
    private void addCellToRow(TableRow row, String text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        row.addView(textView);
    }


    /**
     * Mostra un messaggio Toast con il messaggio specificato.
     *
     * @param message Il messaggio da mostrare nel Toast.
     */
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}