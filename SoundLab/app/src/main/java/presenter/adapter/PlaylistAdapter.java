package presenter.adapter;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundlab.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import model.Playlist;
import view.CustomCardView;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private final ArrayList<Playlist> playlistArrayList;

    // Costruttore per inizializzare l'adapter con la lista di playlist
    public PlaylistAdapter(Context context, ArrayList<Playlist> playlistArrayList) {
        this.playlistArrayList = playlistArrayList;
        updatePlaylistOrder(); // Ordina le playlist iniziali
    }

    // Metodo per aggiornare l'ordine delle playlist in base alle preferenze
    public void updatePlaylistOrder() {
        playlistArrayList.sort((playlist1, playlist2) -> {
            // Ordina per preferenza (favorite prima), poi per nome
            if (playlist1.isFavorite() == playlist2.isFavorite()) {
                // Se hanno lo stesso stato di preferenza, ordina per nome
                return playlist1.getName().compareToIgnoreCase(playlist2.getName());
            } else {
                // Altrimenti, ordina per preferenza
                return Boolean.compare(playlist2.isFavorite(), playlist1.isFavorite());
            }
        });
    }

    // Metodo chiamato quando RecyclerView ha bisogno di un nuovo ViewHolder
    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla il layout per ciascun elemento della RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_cardview, parent, false);
        return new ViewHolder(view);
    }

    // Metodo chiamato per visualizzare i dati in una posizione specifica
    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
        // Ottieni la playlist dalla posizione corrente
        Playlist playlist = playlistArrayList.get(position);

        // Popola il ViewHolder con i dati della playlist
        holder.playlistImage.setImageResource(playlist.getImage());
        holder.playlistName.setText(playlist.getName());
        holder.playlistGenere.setText(playlist.getGenere());
        holder.favouriteButton.setChecked(playlist.isFavorite());

        // Gestisci il cambiamento di preferenza quando il pulsante Preferito viene selezionato/deselezionato
        holder.favouriteButton.setOnCheckedChangeListener((buttonView, isChecked) -> {

            // Ottieni la posizione corrente nell'adattatore
            int adapterPosition = holder.getAdapterPosition();

            // Verifica se la posizione è valida
            if (adapterPosition != RecyclerView.NO_POSITION) {

                // Ottieni la playlist selezionata
                Playlist selectedPlaylist = playlistArrayList.get(adapterPosition);

                // Aggiorna lo stato di preferenza nel modello dei dati
                selectedPlaylist.setFavorite(isChecked);

                // Rimuovi l'elemento dalla posizione corrente
                playlistArrayList.remove(adapterPosition);

                // Aggiungi la playlist nella posizione corretta (rispettando l'ordinamento)
                int index = addPlaylistInOrder(selectedPlaylist, isChecked);

                // Notifica che l'elemento è stato spostato
                notifyItemMoved(adapterPosition, index);
            }
        });

        // Gestisce il long click su un elemento della RecyclerView
        holder.itemView.setOnLongClickListener(v -> {
            // Ottiene la playlist specifica associata all'elemento
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Playlist selectedPlaylist = playlistArrayList.get(adapterPosition);

                // Crea il BottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(holder.itemView.getContext());
                View bottomSheetView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.bottom_popup_playlist, (ViewGroup) holder.itemView, false);

                // Inizializza gli elementi del layout del pannello inferiore
                ImageView playlist_image = bottomSheetView.findViewById(R.id.playlist_image);
                playlist_image.setImageResource(selectedPlaylist.getImage());
                TextView playlist_name = bottomSheetView.findViewById(R.id.playlist_name);
                playlist_name.setText(selectedPlaylist.getName());
                TextView playlist_genere = bottomSheetView.findViewById(R.id.playlist_genere);
                playlist_genere.setText(selectedPlaylist.getGenere());
                CustomCardView elimina = bottomSheetView.findViewById(R.id.elimina);
                CustomCardView rinomina = bottomSheetView.findViewById(R.id.rinomina);
                CustomCardView cambia_genere = bottomSheetView.findViewById(R.id.cambia_genere);

                // Imposta il listener del bottone Elimina
                elimina.setOnClickListener(v0 -> {
                    // Esegue le azioni necessarie per l'eliminazione utilizzando la playlist specifica
                    playlistArrayList.remove(selectedPlaylist);
                    notifyItemRemoved(adapterPosition);
                    // ...

                    // Chiude il BottomSheetDialog
                    bottomSheetDialog.dismiss();
                });

                // Imposta il listener del bottone Rinomina
                rinomina.setOnClickListener(v1 -> {
                    // ...

                });

                // Imposta il listener del bottone Cambia Genere
                cambia_genere.setOnClickListener(v2 -> {
                    // ...

                });

                // Mostra il BottomSheetDialog
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
            // Restituisce true per indicare che il long click è stato gestito
            return true;
        });
    }


    public int addPlaylistInOrder(Playlist playlist, boolean isFavorite) {
        // Inizializza l'indice in cui verrà inserita la nuova playlist
        int index = 0;

        // Scorre tutte le playlist esistenti per trovare la posizione corretta
        for (Playlist p : playlistArrayList) {
            if (isFavorite && p.isFavorite()) {
                // Se stiamo aggiungendo tra i preferiti e troviamo una playlist preferita,
                // controlla l'ordine alfabetico e inserisci prima se necessario
                if (playlist.getName().compareToIgnoreCase(p.getName()) < 0) {
                    break;
                }
            } else if (!isFavorite && !p.isFavorite()) {
                // Se stiamo aggiungendo tra i non preferiti e troviamo una playlist non preferita,
                // controlla l'ordine alfabetico e inserisci prima se necessario
                if (playlist.getName().compareToIgnoreCase(p.getName()) < 0) {
                    break;
                }
            } else if (isFavorite && !p.isFavorite()) {
                // Se stiamo aggiungendo tra i preferiti e troviamo una playlist non preferita,
                // interrompi perché le preferite devono essere inserite prima delle non preferite
                break;
            }

            // Incrementa l'indice per indicare la posizione corrente
            index++;
        }

        // Aggiungi la playlist nella posizione corretta
        playlistArrayList.add(index, playlist);

        // Restituisci l'indice della nuova playlist inserita
        return index;
    }

    // Restituisci il numero totale di elementi nella RecyclerView
    @Override
    public int getItemCount() {
        return playlistArrayList.size();
    }

    // ViewHolder che contiene i riferimenti agli elementi della playlist
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView playlistImage;
        private final TextView playlistName;
        private final TextView playlistGenere;
        private final ToggleButton favouriteButton;

        // Costruttore che inizializza i riferimenti agli elementi della playlist
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistImage = itemView.findViewById(R.id.playlist_image);
            playlistName = itemView.findViewById(R.id.playlist_name);
            playlistGenere = itemView.findViewById(R.id.playlist_genere);
            favouriteButton = itemView.findViewById(R.id.favourite_button);
        }
    }
}
