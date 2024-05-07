package com.soundlab.app.singleton;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;

import com.soundlab.app.model.Song;

import java.util.ArrayList;

public class PlayerSingleton {

    private static volatile PlayerSingleton instance;
    private MediaPlayer mediaPlayer;
    private Song song;
    private boolean created = false;
    private boolean playing = false;
    private final int INTERVAL = 3500;
    private ArrayList<Song> songArrayList = new ArrayList<>();
    private int songPosition = 0;

    private PlayerSingleton() {
    }

    public static PlayerSingleton getInstance() {
        if (instance == null) {
            synchronized (PlayerSingleton.class) {
                if (instance == null) {
                    instance = new PlayerSingleton();
                }
            }
        }
        return instance;
    }

    public void start(@NonNull Context context) {
        if (!isCreated()) {
            try {
                song = songArrayList.get(songPosition);
                mediaPlayer = MediaPlayer.create(context, song.getTrack());
                created = true;
                sendBroadcast(context, "MEDIAPLAYER_INIT");
                mediaPlayer.setOnCompletionListener(mp -> next(context));
                playAndPause(context);
            } catch (Exception e) {
                e.printStackTrace();
                sendBroadcast(context, "PLAYBACK_ERROR");
            }
        } else {
            mediaPlayer.reset();
            releaseMediaPlayer();
            try {
                song = songArrayList.get(songPosition);
                mediaPlayer = MediaPlayer.create(context, song.getTrack());
                created = true;
                sendBroadcast(context, "MEDIAPLAYER_INIT");
                mediaPlayer.setOnCompletionListener(mp -> next(context));
                playAndPause(context);
            } catch (Exception e) {
                e.printStackTrace();
                sendBroadcast(context, "PLAYBACK_ERROR");
            }
        }
    }

    private void restart(@NonNull Context context) {
        if (isCreated()) {
            mediaPlayer.reset();
            releaseMediaPlayer();
            try {
                song = songArrayList.get(songPosition);
                mediaPlayer = MediaPlayer.create(context, song.getTrack());
                created = true;
                mediaPlayer.setOnCompletionListener(mp -> next(context));
                playAndPause(context);
            } catch (Exception e) {
                e.printStackTrace();
                sendBroadcast(context, "PLAYBACK_ERROR");
            }
        }
    }

    public void playAndPause(Context context) {
        if (isCreated()) {
            if (!isPlaying()) {
                playing = true;
                sendBroadcast(context, "PLAYBACK_STATE_CHANGED");
                mediaPlayer.start();
            } else {
                sendBroadcast(context, "PLAYBACK_STATE_CHANGED");
                mediaPlayer.pause();
                playing = false;
            }
        }
    }


    public void previous(@NonNull Context context) {
        if (isCreated()) {
            if (songPosition > 0 && mediaPlayer.getCurrentPosition() < INTERVAL) {
                songPosition--;
                song = songArrayList.get(songPosition);
                restart(context);
                sendBroadcast(context, "SONG_CHANGED");
            } else {
                restart(context);
                sendBroadcast(context, "PLAYBACK_RESTART");
            }
        }
    }

    public void next(@NonNull Context context) {
        if (isCreated()) {
            if (songPosition < songArrayList.size() - 1) {
                songPosition++;
                song = songArrayList.get(songPosition);
                restart(context);
                sendBroadcast(context, "SONG_CHANGED");
            } else {
                sendBroadcast(context, "PLAYBACK_FINISH");
                if (mediaPlayer.getDuration() <= mediaPlayer.getCurrentPosition() + 1000) {
                    playing = false;
                    sendBroadcast(context, "PLAYBACK_STATE_CHANGED");
                }
            }
        }
    }

    public void releaseMediaPlayer() {
        if (isCreated()) {
            mediaPlayer.release();
            mediaPlayer = null;
            song = null;
            created = false;
            playing = false;
        }
    }

    private void sendBroadcast(Context context, String intentTag) {
        Intent broadcastIntent = new Intent(intentTag);
        context.sendBroadcast(broadcastIntent);
    }

    public void setSongArrayList(ArrayList<Song> songArrayList) {
        this.songArrayList = songArrayList;
    }

    public void setSongPosition(int songPosition) {
        this.songPosition = songPosition;
    }

    public Song getSong() {
        return song;
    }

    private boolean isCreated() {
        return created;
    }

    public boolean isPlaying() {
        return playing;
    }


    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
