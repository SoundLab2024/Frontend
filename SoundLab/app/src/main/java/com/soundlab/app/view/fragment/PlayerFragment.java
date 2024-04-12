package com.soundlab.app.view.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.soundlab.R;
import com.google.android.material.snackbar.Snackbar;
import com.soundlab.app.model.Song;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.CustomButton;
import com.soundlab.app.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.Locale;

public class PlayerFragment extends Fragment {

    // Componenti UI
    private MediaPlayer mediaPlayer;
    private CustomButton playButton;
    private SeekBar seekBar;
    private TextView timePassed;
    private TextView timeRemaining;
    private CustomButton nextButton;
    private CustomButton previousButton;
    private TextView title;
    private TextView artist;
    private ImageView cover;
    private boolean isPlaying = true;
    private int songPosition;
    private ArrayList<Song> songArrayList;
    private Song song;

    // Gestione dell'aggiornamento periodico della barra di avanzamento
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateSeekBarAndTimeRunnable;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        Log.d("PlayerFragment", "onCreateView called");

        Bundle bundle = getArguments();

        if (bundle != null) {
            initializeSongData(bundle);
            initializeMediaPlayerComponents(view);
            startPlayback();
        }

        hideBottomNavigationView();

        return view;
    }

    public void hideBottomNavigationView() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideBottomNavigationView();
        }
    }

    /**
     * Metodo per inizializzare i dati del brano in base al bundle fornito.
     *
     * @param bundle Il bundle contenente i dati del brano
     */
    private void initializeSongData(Bundle bundle) {
        if (mediaPlayer == null) {
            songPosition = bundle.getInt("songPosition");
            Object[] objectArray = (Object[]) bundle.getSerializable("songArrayList");

            if (objectArray != null) {
                songArrayList = new ArrayList<>(objectArray.length);
                for (Object obj : objectArray) {
                    if (obj instanceof Song) {
                        songArrayList.add((Song) obj);
                    }
                }
            }

            song = songArrayList.get(songPosition);
        }
    }

    /**
     * Metodo per inizializzare i componenti del MediaPlayer.
     *
     * @param view La vista radice del fragment
     */
    private void initializeMediaPlayerComponents(View view) {

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getActivity(), song.getTrack());
        }

        playButton = view.findViewById(R.id.play);
        seekBar = view.findViewById(R.id.seekBar);
        timePassed = view.findViewById(R.id.timePassed);
        timeRemaining = view.findViewById(R.id.timeRemaining);
        nextButton = view.findViewById(R.id.next);
        previousButton = view.findViewById(R.id.previous);

        CustomButton addToPlaylistButton = view.findViewById(R.id.addToPlaylists);
        addToPlaylistButton.setOnClickListener(view1 -> loadAddToPlaylistFragment(song));


        initMediaPlayer();
        initSeekBar();
    }

    /**
     * Metodo per caricare il fragment AddToPlaylist con il brano selezionato.
     *
     * @param song Il brano selezionato
     */
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

    /**
     * Metodo per avviare la riproduzione del brano corrente.
     */
    private void startPlayback() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.start();
            playButton.setBackgroundResource(R.drawable.pause);
        }
    }

    /**
     * Metodo per inizializzare il MediaPlayer e i suoi listener.
     */
    private void initMediaPlayer() {
        playButton.setOnClickListener(v -> togglePlayPause());
        mediaPlayer.setOnCompletionListener(mp -> onPlaybackCompleted());
        mediaPlayer.setOnPreparedListener(mp -> {
            seekBar.setMax(mediaPlayer.getDuration());
            updateSeekBarAndTime();
        });
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            showToast("Errore nella riproduzione del brano.");
            return false;
        });
    }

    /**
     * Metodo per inizializzare la SeekBar e il suo listener.
     */
    private void initSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    updateTimeLabels(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /**
     * Metodo per gestire la riproduzione e la pausa del brano.
     */
    private void togglePlayPause() {
        if (isPlaying) {
            mediaPlayer.pause();
            playButton.setBackgroundResource(R.drawable.play);
        } else {
            if (mediaPlayer.getCurrentPosition() == mediaPlayer.getDuration()) {
                restartPlayback();
            }
            mediaPlayer.start();
            playButton.setBackgroundResource(R.drawable.pause);
        }
        isPlaying = !isPlaying;
        updateSeekBarAndTime();
    }

    /**
     * Metodo per riavviare la riproduzione del brano corrente.
     */
    private void restartPlayback() {
        mediaPlayer.seekTo(0);
        seekBar.setProgress(0);
        updateTimeLabels(0);
    }

    /**
     * Metodo chiamato alla fine della riproduzione del brano.
     */
    private void onPlaybackCompleted() {
        playButton.setBackgroundResource(R.drawable.play);
        isPlaying = false;
        playNextSong();
    }

    /**
     * Metodo per aggiornare la barra di avanzamento e i timer del brano.
     */
    private void updateSeekBarAndTime() {
        if (mediaPlayer != null && isPlaying) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            seekBar.setProgress(currentPosition);
            updateTimeLabels(currentPosition);
            handler.postDelayed(this::updateSeekBarAndTime, 1000);
        }
    }


    /**
     * Metodo per aggiornare i timer del brano.
     *
     * @param currentPosition La posizione corrente del brano in millisecondi
     */
    private void updateTimeLabels(int currentPosition) {
        timePassed.setText(millisecondsToTime(currentPosition));
        timeRemaining.setText(String.format(Locale.getDefault(), "-%s", millisecondsToTime(mediaPlayer.getDuration() - currentPosition)));
    }

    /**
     * Metodo per convertire millisecondi in formato di tempo (MM:SS).
     *
     * @param milliseconds Il tempo in millisecondi da convertire
     * @return Il tempo convertito in formato MM:SS
     */
    private String millisecondsToTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.changeStatusBarColorFragment(this, R.color.dark_purple);

        initializeSongDetails(view);

        nextButton.setOnClickListener(v -> playNextSong());
        previousButton.setOnClickListener(v -> playPreviousSong());

        // Aggiorna la SeekBar e i timer del brano
        if (mediaPlayer != null) {
            seekBar.setMax(mediaPlayer.getDuration());
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            updateTimeLabels(mediaPlayer.getCurrentPosition());
            updateSeekBarAndTime();
        }
    }

    /**
     * Metodo per inizializzare i dettagli del brano come il titolo, l'artista e la copertina.
     *
     * @param view La vista radice del fragment
     */
    private void initializeSongDetails(View view) {
        title = view.findViewById(R.id.title);
        artist = view.findViewById(R.id.artist);
        cover = view.findViewById(R.id.cover);

        title.setText(song.getName());
        artist.setText(song.getArtists().get(0).getName());
        cover.setImageResource(song.getImage());
    }

    /**
     * Metodo per riprodurre il brano successivo nella lista.
     */
    private void playNextSong() {
        if (songPosition < songArrayList.size() - 1) {
            songPosition++;
            song = songArrayList.get(songPosition);
            animateSongChange();
        } else {
            showToast("Non ci sono altri brani da riprodurre.");
        }
    }

    /**
     * Metodo per riprodurre il brano precedente nella lista.
     */
    private void playPreviousSong() {
        if (mediaPlayer.getCurrentPosition() >= 3500) {
            restartMediaPlayer();
        } else if (songPosition > 0) {
            songPosition--;
            song = songArrayList.get(songPosition);
            animateSongChange();
        } else {
            showToast("Non ci sono altri brani da riprodurre.");
        }
    }

    /**
     * Metodo per animare il cambiamento del brano.
     */
    private void animateSongChange() {
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
        fadeOutAnimation.setDuration(1000);

        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
                fadeInAnimation.setDuration(1000);
                fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                title.startAnimation(fadeInAnimation);
                artist.startAnimation(fadeInAnimation);
                cover.startAnimation(fadeInAnimation);
                updateSongDetails();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        title.startAnimation(fadeOutAnimation);
        artist.startAnimation(fadeOutAnimation);
        cover.startAnimation(fadeOutAnimation);
        new Handler(Looper.getMainLooper()).postDelayed(this::restartMediaPlayer, 1100);
    }

    /**
     * Metodo per aggiornare i dettagli del brano corrente.
     */
    private void updateSongDetails() {
        title.setText(song.getName());
        artist.setText(song.getArtists().get(0).getName());
        cover.setImageResource(song.getImage());
    }

    /**
     * Metodo per riavviare il MediaPlayer con il brano corrente.
     */
    private void restartMediaPlayer() {
        mediaPlayer.release();
        mediaPlayer = MediaPlayer.create(getActivity(), song.getTrack());
        mediaPlayer.start();
        playButton.setBackgroundResource(R.drawable.pause);
        isPlaying = true;
        initMediaPlayer();
        initSeekBar();
    }

    /**
     * Metodo per mostrare un toast personalizzato.
     */
    private void showToast(String message) {
        Snackbar snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();

        // Centra il testo
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        snackbar.show();
    }

    @Override
    public void onResume() {
        Log.d("songPosition", String.valueOf(songPosition));
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMediaPlayerResources();
    }

    /**
     * Metodo per rilasciare le risorse del MediaPlayer.
     */
    private void releaseMediaPlayerResources() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (updateSeekBarAndTimeRunnable != null) {
            handler.removeCallbacks(updateSeekBarAndTimeRunnable);
            updateSeekBarAndTimeRunnable = null;
        }
    }
}