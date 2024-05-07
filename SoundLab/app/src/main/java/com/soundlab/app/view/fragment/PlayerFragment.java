package com.soundlab.app.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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
import com.soundlab.app.service.PlayerService;
import com.soundlab.app.singleton.PlayerSingleton;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.CustomButton;
import com.soundlab.app.view.activity.MainActivity;

import java.util.Objects;

public class PlayerFragment extends Fragment {
    private final PlayerSingleton playerSingleton = PlayerSingleton.getInstance();
    private CustomButton playButton;
    private SeekBar seekBar;
    private TextView timePassed;
    private TextView timeRemaining;
    private CustomButton nextButton;
    private CustomButton previousButton;
    private TextView title;
    private TextView artist;
    private BroadcastReceiver receiver;
    private final Handler handler = new Handler();
    private Runnable runnable;
    private ImageView cover;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Objects.equals(intent.getAction(), "MEDIAPLAYER_INIT")) {
                    updateSongLabel();
                    updatePlayButton();
                    initSeekBar();
                    startSeekBarUpdate();
                }
                else if (Objects.equals(intent.getAction(), "PLAYBACK_STATE_CHANGED")) {
                    // Aggiorna l'interfaccia grafica del fragment
                    updatePlayButton();
                } else if (Objects.equals(intent.getAction(), "SONG_CHANGED")){
                    initSeekBar();
                    animateSongChange();
                } else if (Objects.equals(intent.getAction(), "PLAYBACK_RESTART")){
                    initSeekBar();
                }
                else if (Objects.equals(intent.getAction(), "PLAYBACK_FINISH")) {
                    showToast("Non ci sono altri brani da riprodurre.");
                } else if (Objects.equals(intent.getAction(), "PLAYBACK_ERROR")) {
                    showToast("Impossibile riprodurre il brano.");
                }
            }
        };


        IntentFilter filter = new IntentFilter();
        filter.addAction("MEDIAPLAYER_INIT");
        filter.addAction("PLAYBACK_STATE_CHANGED");
        filter.addAction("SONG_CHANGED");
        filter.addAction("PLAYBACK_FINISH");
        filter.addAction("PLAYBACK_ERROR");
        filter.addAction("PLAYBACK_RESTART");
        requireContext().registerReceiver(receiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        Log.d("PlayerFragment", "onCreateView called");
        hideBottomNavigationView();

        Bundle bundle = getArguments();

        if (bundle != null) {
            boolean avoidServiceRestart = bundle.getBoolean("avoidServiceRestart", false);
            if (!avoidServiceRestart) {
                invokeMusicService();
            }

        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.changeStatusBarColorFragment(this, R.color.dark_purple);

        initializeMediaPlayerComponents(view);
        initPlayback();
        initSeekBar();
    }

    public void hideBottomNavigationView() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideBottomNavigationView();
        }
    }

    private void initializeMediaPlayerComponents(View view) {
        playButton = view.findViewById(R.id.play);
        seekBar = view.findViewById(R.id.seekBar);
        timePassed = view.findViewById(R.id.timePassed);
        timeRemaining = view.findViewById(R.id.timeRemaining);
        nextButton = view.findViewById(R.id.next);
        previousButton = view.findViewById(R.id.previous);
        title = view.findViewById(R.id.title);
        artist = view.findViewById(R.id.artist);
        cover = view.findViewById(R.id.cover);

        CustomButton addToPlaylistButton = view.findViewById(R.id.addToPlaylists);
        addToPlaylistButton.setOnClickListener(view1 -> loadAddToPlaylistFragment(playerSingleton.getSong()));
    }

    private void updateSongLabel() {
        Song song = playerSingleton.getSong();

        if (song != null) {
            title.setText(song.getName());
            artist.setText(song.getArtists().get(0).getName());
        }
    }

    private void initPlayback() {
        playButton.setOnClickListener(v -> togglePlayback());
        nextButton.setOnClickListener(v -> playerSingleton.next(requireContext()));
        previousButton.setOnClickListener(v -> playerSingleton.previous(requireContext()));
    }

    private void togglePlayback() {
        playerSingleton.playAndPause(requireContext());
        updatePlayButton();
    }

    private void updatePlayButton() {
        if (playerSingleton.isPlaying()) {
            playButton.setBackgroundResource(R.drawable.pause);
        } else {
            playButton.setBackgroundResource(R.drawable.play);
        }
    }

    private void initSeekBar() {
        MediaPlayer mediaPlayer = playerSingleton.getMediaPlayer();

        if (mediaPlayer != null) {

            seekBar.setMax(mediaPlayer.getDuration());
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            updateTimeLabels();
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        mediaPlayer.seekTo(progress);
                        updateTimeLabels();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });
        }
    }

    private void updateTimeLabels() {

        int totalTime = playerSingleton.getMediaPlayer().getDuration();
        int currentTime = playerSingleton.getMediaPlayer().getCurrentPosition();
        int remaningTime = totalTime - currentTime;

        String remaningTimeString = Utilities.milliSecondsToTimer(remaningTime);
        String currentTimeString = Utilities.milliSecondsToTimer(currentTime);

        timePassed.setText(currentTimeString);
        timeRemaining.setText("-" + remaningTimeString);

    }

    private void startSeekBarUpdate() {
        if (playerSingleton.getMediaPlayer() != null) {
            if (runnable == null) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (playerSingleton.isPlaying()) {
                            int currentPosition = playerSingleton.getMediaPlayer().getCurrentPosition();
                            seekBar.setProgress(currentPosition);
                            updateTimeLabels();
                        }
                        handler.postDelayed(this, 1000); // Aggiorna ogni secondo
                    }
                };
                handler.postDelayed(runnable, 1000); // Avvia l'aggiornamento ogni secondo
            }
        }
    }

    private void invokeMusicService() {
        Intent intent = new Intent(requireContext(), PlayerService.class);
        requireContext().startService(intent);
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

    private void showToast(String message) {
        Snackbar snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();

        // Center the text
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        snackbar.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePlayButton();
        updateSongLabel();
        startSeekBarUpdate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.setArguments(null);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        requireContext().unregisterReceiver(receiver);
    }

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
                        updateSongLabel();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // Ritarda l'esecuzione di updateSongLabel() di 2 secondi
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                title.startAnimation(fadeInAnimation);
                artist.startAnimation(fadeInAnimation);
                cover.startAnimation(fadeInAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        title.startAnimation(fadeOutAnimation);
        artist.startAnimation(fadeOutAnimation);
        cover.startAnimation(fadeOutAnimation);
    }


}
