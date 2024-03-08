package utils;

import android.app.Activity;
import android.view.Window;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import model.Artist;
import model.Song;

public class Utilities {

    public static final String homeFragmentTag = "home";
    public static final String searchFragmentTag = "search";
    public static final String profileFragmentTag = "profile";
    public static final String playlistFragmentTag = "playlist";
    public static final String addToPlaylistFragmentTag = "addToPlaylist";
    public static final String artistFragmentTag  = "artist";

    /**
     * Imposta il colore della barra di stato per un'Activity.
     *
     * @param activity L'istanza dell'Activity corrente.
     * @param color    Il colore desiderato della barra di stato.
     */
    public static void changeStatusBarColor(Activity activity, int color) {
        Window window = activity.getWindow();
        int statusBarColor = ContextCompat.getColor(activity, color);

        window.setStatusBarColor(statusBarColor);
    }

    /**
     * Imposta il colore della barra di stato per un Fragment, utilizzando l'Activity associata.
     *
     * @param fragment Il Fragment corrente.
     * @param color    Il colore desiderato della barra di stato.
     */
    public static void changeStatusBarColorFragment(Fragment fragment, int color) {
        // Verifica che il Fragment sia associato a un'Activity prima di impostare il colore della barra di stato
        if (fragment.getActivity() != null) {
            // Richiama il metodo changeStatusBarColor passando l'Activity associata al Fragment
            changeStatusBarColor(fragment.getActivity(), color);
        }
    }

    public static String ottieniArtistiDellaTracciaInStringa(Song song) {
        // Ottieni i relativi artisti
        ArrayList<Artist> artistArrayList = new ArrayList<>(song.getArtists());
        StringBuilder artistString = new StringBuilder();

        for (Artist artist : artistArrayList) {
            artistString.append(artist.getName()).append(", ");
        }

        // Rimuovi l'ultima virgola e lo spazio (se presenti)
        if (artistString.length() > 0) {
            artistString.setLength(artistString.length() - 2);
        }

        return artistString.toString();
    }

}

