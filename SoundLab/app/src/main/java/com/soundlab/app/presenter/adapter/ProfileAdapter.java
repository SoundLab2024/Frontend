package com.soundlab.app.presenter.adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundlab.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import com.soundlab.app.model.Playlist;
import com.soundlab.app.view.CustomButton;
import com.soundlab.app.view.CustomCardView;
import com.soundlab.app.view.fragment.ProfileFragment;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private final List<Playlist> playlists;
    private final ProfileFragment profileFragment;

    // Costruttore per inizializzare l'adapter con la lista di playlist
    public ProfileAdapter(ProfileFragment profileFragment, List<Playlist> playlists) {
        this.playlists = playlists;
        this.profileFragment = profileFragment;
        updatePlaylistOrder();
    }

    // Metodo per aggiornare l'ordine delle playlist in base alle preferenze
    public void updatePlaylistOrder() {
        playlists.sort((playlist1, playlist2) -> {
            // Ordina per preferenza (favorite prima), poi per nome
            if (playlist1.isFavourite() == playlist2.isFavourite()) {
                // Se hanno lo stesso stato di preferenza, ordina per nome
                return playlist1.getName().compareToIgnoreCase(playlist2.getName());
            } else {
                // Altrimenti, ordina per preferenza
                return Boolean.compare(playlist2.isFavourite(), playlist1.isFavourite());
            }
        });
    }

    public void addPlaylist(Playlist newPlaylist) {
        // Aggiungi la nuova playlist alla lista e notifica alla UI
        int index = addPlaylistInOrder(newPlaylist, newPlaylist.isFavourite());
        new Handler().post(() -> notifyItemInserted(index));
    }

    // Metodo chiamato quando RecyclerView ha bisogno di un nuovo ViewHolder
    @NonNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla il layout per ciascun elemento della RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_cardview, parent, false);
        return new ViewHolder(view);
    }

    // Metodo chiamato per visualizzare i dati in una posizione specifica
    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {
        // Ottieni la playlist dalla posizione corrente
        Playlist playlist = playlists.get(position);

        //DA ELIMINARE
        playlist.setImage(R.drawable.playlist_default);

        // Popola il ViewHolder con i dati della playlist
        holder.playlistImage.setImageResource(playlist.getImage());
        holder.playlistName.setText(playlist.getName());
        holder.playlistGenere.setText(playlist.getGenre());
        holder.favouriteButton.setChecked(playlist.isFavourite());

        holder.itemView.setOnClickListener(v -> {
            // Apri il fragment PlaylistFragment quando viene cliccata una playlist
            profileFragment.loadPlaylistFragment(playlist);
        });


        // Gestisci il cambiamento di preferenza quando il pulsante Preferito viene selezionato/deselezionato
        holder.favouriteButton.setOnCheckedChangeListener((buttonView, isChecked) -> {

            // Ottieni la posizione corrente nell'adattatore
            int adapterPosition = holder.getAdapterPosition();

            // Verifica se la posizione è valida
            if (adapterPosition != RecyclerView.NO_POSITION) {

                // Ottieni la playlist selezionata
                Playlist selectedPlaylist = playlists.get(adapterPosition);

                // TODO: Aggiorna lo stato di preferenza nel backend

                // Aggiorna lo stato di preferenza nel modello dei dati
                selectedPlaylist.setFavourite(isChecked);

                // Rimuovi l'elemento dalla posizione corrente
                playlists.remove(adapterPosition);

                // Aggiungi la playlist nella posizione corretta (rispettando l'ordinamento)
                int index = addPlaylistInOrder(selectedPlaylist, isChecked);

                // Notifica che l'elemento è stato spostato
                new Handler().post(() -> notifyItemMoved(adapterPosition, index));
            }
        });

        // Gestisce il long click su un elemento della RecyclerView
        holder.itemView.setOnLongClickListener(v -> {
            // Ottiene la playlist specifica associata all'elemento
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Playlist selectedPlaylist = playlists.get(adapterPosition);

                // Crea il BottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(holder.itemView.getContext());
                View bottomSheetView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.bottom_popup_playlist, (ViewGroup) holder.itemView, false);

                // Inizializza gli elementi del layout del pannello inferiore
                initBottomSheetDialogElements(bottomSheetView, selectedPlaylist);
                CustomCardView elimina = bottomSheetView.findViewById(R.id.elimina);
                CustomCardView rinomina = bottomSheetView.findViewById(R.id.rinomina);
                CustomCardView cambia_genere = bottomSheetView.findViewById(R.id.cambia_genere);

                // Imposta il listener del bottone Elimina
                elimina.setOnClickListener(v0 -> {
                    // Chiude il BottomSheetDialog
                    bottomSheetDialog.dismiss();

                    // Esegue le azioni necessarie per l'eliminazione utilizzando la playlist specifica
                    showDialog_confermaElimina(holder.itemView.getContext(), selectedPlaylist, adapterPosition);
                });

                // Imposta il listener del bottone Rinomina
                rinomina.setOnClickListener(v1 -> {
                    // Chiude il BottomSheetDialog
                    bottomSheetDialog.dismiss();

                    // Esegue le azioni necessarie per la rinomina utilizzando la playlist specifica
                    showDialog_rinomina(holder.itemView.getContext(), selectedPlaylist, adapterPosition);
                });

                // Imposta il listener del bottone Cambia Genere
                cambia_genere.setOnClickListener(v2 -> {
                    // Chiude il BottomSheetDialog
                    bottomSheetDialog.dismiss();

                    // Esegue le azioni necessarie per il cambio genere utilizzando la playlist specifica
                    showDialog_cambiaGenere(holder.itemView.getContext(), selectedPlaylist, adapterPosition);

                });

                // Mostra il BottomSheetDialog
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
            // Restituisce true per indicare che il long click è stato gestito
            return true;
        });

    }

    private void initBottomSheetDialogElements(View bottomSheetView, Playlist selectedPlaylist) {
        ImageView playlist_image = bottomSheetView.findViewById(R.id.playlist_image);
        playlist_image.setImageResource(selectedPlaylist.getImage());
        TextView playlist_name = bottomSheetView.findViewById(R.id.playlist_name);
        playlist_name.setText(selectedPlaylist.getName());
        TextView playlist_genere = bottomSheetView.findViewById(R.id.genere);
        playlist_genere.setText(selectedPlaylist.getGenre());
    }


    private int addPlaylistInOrder(Playlist playlist, boolean isFavorite) {
        // Inizializza l'indice in cui verrà inserita la nuova playlist
        int index = 0;

        // Scorre tutte le playlist esistenti per trovare la posizione corretta
        for (Playlist p : playlists) {
            if (isFavorite && p.isFavourite()) {
                // Se stiamo aggiungendo tra i preferiti e troviamo una playlist preferita,
                // controlla l'ordine alfabetico e inserisci prima se necessario
                if (playlist.getName().compareToIgnoreCase(p.getName()) < 0) {
                    break;
                }
            } else if (!isFavorite && !p.isFavourite()) {
                // Se stiamo aggiungendo tra i non preferiti e troviamo una playlist non preferita,
                // controlla l'ordine alfabetico e inserisci prima se necessario
                if (playlist.getName().compareToIgnoreCase(p.getName()) < 0) {
                    break;
                }
            } else if (isFavorite && !p.isFavourite()) {
                // Se stiamo aggiungendo tra i preferiti e troviamo una playlist non preferita,
                // interrompi perché le preferite devono essere inserite prima delle non preferite
                break;
            }

            // Incrementa l'indice per indicare la posizione corrente
            index++;
        }

        // Aggiungi la playlist nella posizione corretta
        playlists.add(index, playlist);

        // Restituisci l'indice della nuova playlist inserita
        return index;
    }

    private void initializeDialogViews(Dialog dialog, Playlist selectedPlaylist) {
        ImageView playlistImage = dialog.findViewById(R.id.playlist_image);
        playlistImage.setImageResource(selectedPlaylist.getImage());

        TextView playlistName = dialog.findViewById(R.id.playlist_name);
        playlistName.setText(selectedPlaylist.getName());

        TextView playlistGenere = dialog.findViewById(R.id.artist);
        playlistGenere.setText(selectedPlaylist.getGenre());
    }

    private void showDialog_confermaElimina(Context context, Playlist selectedPlaylist, int adapterPosition) {
        // Creazione e personalizzazione del Dialog
        Dialog dialog = new Dialog(context, R.style.CustomDialogStyle);
        dialog.setContentView(R.layout.popup_elimina_playlist);

        // Inizializzazione degli elementi del layout del Dialog
        initializeDialogViews(dialog, selectedPlaylist);

        // Inizializzazione dei bottoni del Dialog
        CustomButton conferma_elimina = dialog.findViewById(R.id.elimina);
        CustomButton annulla = dialog.findViewById(R.id.annulla);

        // Listener per il pulsante di conferma eliminazione
        conferma_elimina.setOnClickListener(view -> {

            // TODO: Elimina la playlist dal backend

            // Rimuove la playlist dalla lista e aggiorna la UI
            playlists.remove(selectedPlaylist);
            notifyItemRemoved(adapterPosition);
            // Chiude il Dialog
            dialog.dismiss();
            // Se non ci sono playlist crea la TextView zeroPlaylist
            if (playlists.isEmpty()) {
                profileFragment.createZeroPlaylistTextView();
            }
        });

        // Listener per il pulsante di annullamento
        annulla.setOnClickListener(view -> {
            // Chiude il Dialog senza effettuare alcuna azione
            dialog.dismiss();
        });


        // Mostra il Dialog
        dialog.show();
    }

    private void showDialog_rinomina(Context context, Playlist selectedPlaylist, int adapterPosition) {
        // Creazione e personalizzazione del Dialog
        Dialog dialog = new Dialog(context, R.style.CustomDialogStyle);
        dialog.setContentView(R.layout.popup_rinomina_playlist);

        // Inizializzazione degli elementi del layout del Dialog
        initializeDialogViews(dialog, selectedPlaylist);

        // Inizializzazione degli elementi di input e bottoni del Dialog
        EditText playlist_input = dialog.findViewById(R.id.email_input);
        CustomButton rinomina = dialog.findViewById(R.id.rinomina);
        CustomButton annulla = dialog.findViewById(R.id.annulla);

        // Listener per il pulsante di rinomina
        rinomina.setOnClickListener(view -> {
            // Ottiene il nuovo nome dalla casella di input
            String nuovo_nome_playlist = playlist_input.getText().toString();
            if (!nuovo_nome_playlist.isEmpty()) {

                // TODO: Cambia il nome della playlist nel backend

                // Aggiorna il nome della playlist, la UI e la posizione nella lista
                selectedPlaylist.setName(nuovo_nome_playlist);
                new Handler().post(() -> notifyItemChanged(adapterPosition));
                playlists.remove(adapterPosition);
                int index = addPlaylistInOrder(selectedPlaylist, selectedPlaylist.isFavourite());
                new Handler().post(() -> notifyItemMoved(adapterPosition, index));
                dialog.dismiss();
            } else {
                // Visualizza un messaggio Toast se il nome è vuoto
                Toast toast = Toast.makeText(context, "Inserisci un nome valido", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        // Listener per il pulsante di annullamento
        annulla.setOnClickListener(view -> {
            // Chiude il Dialog senza effettuare alcuna azione
            dialog.dismiss();
        });

        // Mostra il Dialog
        dialog.show();
    }

    private void showDialog_cambiaGenere(Context context, Playlist selectedPlaylist, int adapterPosition) {
        // Creazione e personalizzazione del Dialog
        Dialog dialog = new Dialog(context, R.style.CustomDialogStyle);
        dialog.setContentView(R.layout.popup_rinomina_playlist);

        // Inizializzazione degli elementi del layout del Dialog
        initializeDialogViews(dialog, selectedPlaylist);

        // Inizializzazione e modifica degli elementi di input e bottoni del Dialog
        EditText playlist_input = dialog.findViewById(R.id.email_input);
        playlist_input.setHint("Inserisci il nuovo genere qui.");
        CustomButton rinomina = dialog.findViewById(R.id.rinomina);
        rinomina.setText("Cambia");
        CustomButton annulla = dialog.findViewById(R.id.annulla);

        // Listener per il pulsante rinomina
        rinomina.setOnClickListener(view -> {
            // Ottiene il nuovo genere dalla casella di input
            String nuovo_genere = playlist_input.getText().toString();
            if (!nuovo_genere.isEmpty()) {

                // TODO: Cambia il genere della playlist nel backend

                // Aggiorna il genere e la UI
                selectedPlaylist.setGenre(nuovo_genere);
                new Handler().post(() -> notifyItemChanged(adapterPosition));
                dialog.dismiss();
            } else {
                // Visualizza un messaggio Toast se il genere è vuoto
                Toast toast = Toast.makeText(context, "Inserisci un genere valido", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        // Listener per il pulsante di annullamento
        annulla.setOnClickListener(view -> {
            // Chiude il Dialog senza effettuare alcuna azione
            dialog.dismiss();
        });

        // Mostra il Dialog
        dialog.show();
    }


    // Restituisci il numero totale di elementi nella RecyclerView
    @Override
    public int getItemCount() {
        return playlists.size();
    }

    // ViewHolder che contiene i riferimenti agli elementi della playlist
    public static class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView playlistImage;
        final TextView playlistName;
        final TextView playlistGenere;
        final ToggleButton favouriteButton;

        // Costruttore che inizializza i riferimenti agli elementi della playlist
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistImage = itemView.findViewById(R.id.playlist_image);
            playlistName = itemView.findViewById(R.id.playlist_name);
            playlistGenere = itemView.findViewById(R.id.artist);
            favouriteButton = itemView.findViewById(R.id.favourite_button);
        }
    }
}
