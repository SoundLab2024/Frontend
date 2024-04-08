package com.soundlab.app.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.soundlab.R;
import com.soundlab.app.model.Artist;
import com.soundlab.app.model.Listening;
import com.soundlab.app.view.CustomButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AnaliticheFragment extends Fragment {

    private List<Listening> listeningList;
    private List<Listening> songStatsList;
    private EditText searchInput;
    private String songTitle;
    private boolean isTableVisible = false;
    private boolean searchByUser = true;
    private boolean searchSong = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inizializzazione delle liste di UserStats e SongStats con dati fittizi
        listeningList = new ArrayList<>();
        listeningList.add(new Listening("Utente1",null,null,null,10,"Mattina"));
        listeningList.add(new Listening("Utente2",null,null,null,11,"Pomeriggio"));
        listeningList.add(new Listening("Utente3",null,null,null,12,"Notte"));



        List<Artist> singers = new ArrayList<>();
        singers.add(new Artist(1, "Artista1",new Date(2002,12,12),"Italiano"));

        songStatsList = new ArrayList<>();
        songStatsList.add(new Listening(null,"Canzone1","Original",null,11,null));
        songStatsList.add(new Listening(null,"Canzone2","Original",null,10,null));
        songStatsList.add(new Listening(null,"Canzone3","Original",null,20,null));


        View view = inflater.inflate(R.layout.fragment_analitiche, container, false);

        // Definizione del RadioGroup
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        RadioButton userRadioButton = view.findViewById(R.id.user_radio_button);
        RadioButton songRadioButton = view.findViewById(R.id.song_radio_button);

        searchInput = view.findViewById(R.id.search);
        songTitle = searchInput.getText().toString();

        // Implementazione della ricerca
        CustomButton searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                performSearch(searchInput.getText().toString());
            }
        });

        // Listener per il cambio di selezione nel RadioGroup
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Controlla quale radio button è stato selezionato e imposta il flag corrispondente
            if (checkedId == userRadioButton.getId()) {
                searchByUser = true;
                searchSong = false;
            } else if (checkedId == songRadioButton.getId()) {
                searchByUser = false;
                searchSong = true;
            }
        });

        return view;
    }

    // Filtro la ricerca
    private void performSearch(String query) {
        if (query.isEmpty()) {
            Toast.makeText(getContext(), "Inserisci elementi validi per la ricerca", Toast.LENGTH_SHORT).show();
            return;
        } else if (searchByUser && !searchSong) {
            searchUser(query);
        } else if (!searchByUser && searchSong) {
            searchSong(query);
        } else {
            Toast.makeText(getContext(), "Errore: Seleziona una categoria valida per la ricerca", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchUser(String query) {
        List<Listening> searchResults = new ArrayList<>();
        for (Listening user : listeningList) {
            if (user.getUtente() != null && user.getUtente().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(user);
            }
        }

        if (searchResults.isEmpty()) {
            Toast.makeText(getContext(), "Nessun risultato trovato", Toast.LENGTH_SHORT).show();
        } else {
            displaySearchResults(searchResults);
        }
    }

    private void searchSong(String query) {
        List<Listening> searchResults = new ArrayList<>();
        for (Listening song : songStatsList) {
            if (song.getCanzone() != null && song.getCanzone().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(song);
            }
        }

        if (searchResults.isEmpty()) {
            Toast.makeText(getContext(), "Nessun risultato trovato", Toast.LENGTH_SHORT).show();
        } else {
            displaySearchResults(searchResults);
        }
    }



    private void displaySearchResults(List<Listening> searchResults) {
        TableLayout tableLayout = getView().findViewById(R.id.table);

        // Rimuove le righe precedenti
        tableLayout.removeAllViews();

        List<String> columns;
        if (searchByUser) {
            columns = Arrays.asList("ㅤ", "Utente","ㅤ","Numero Ascolti", "Fascia Oraria");
        } else {
            columns = Arrays.asList("ㅤ", "Canzone", "Tipo", "Numero Ascolti");
        }

        // Aggiungi la riga delle intestazioni
        TableRow headerRow = new TableRow(getContext());
        for (String columnName : columns) {
            TextView headerTextView = new TextView(getContext());
            headerTextView.setText(columnName);
            headerRow.addView(headerTextView);
        }
        tableLayout.addView(headerRow);

        // Aggiungi righe alla tabella per ogni risultato della ricerca
        for (int i = 0; i < searchResults.size(); i++) {
            Listening listening = searchResults.get(i);
            // Creo una nuova riga
            TableRow row = new TableRow(getContext());

            // Aggiunta il numero di riga
            TextView numberTextView = new TextView(getContext());
            numberTextView.setText(String.valueOf(i + 1)); // Inizia da 1 invece di 0
            row.addView(numberTextView);

            // Aggiunta celle dei dati corrispondenti
            if (searchByUser) {
                addCellToRow(row, listening.getUtente());
                addCellToRow(row, listening.getSongType());
                addCellToRow(row, String.valueOf(listening.getTotalListens()));
                addCellToRow(row, listening.getTimeSlot());
            } else {
                addCellToRow(row, listening.getCanzone());
                addCellToRow(row, listening.getSongType());
                addCellToRow(row, String.valueOf(listening.getTotalListens()));
                addCellToRow(row, listening.getTimeSlot());
            }

            // Aggiungo la riga alla tabella
            tableLayout.addView(row);
        }
    }
    private void addCellToRow(TableRow row, String text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        row.addView(textView);
    }
}