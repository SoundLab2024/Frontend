package com.soundlab.app.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.example.soundlab.R;
import com.soundlab.app.model.Song;
import com.soundlab.app.singleton.PlayerSingleton;
import com.soundlab.app.view.activity.MainActivity;

import java.util.Objects;

public class PlayerService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "MusicPlayerChannel";
    PlayerSingleton playerSingleton = PlayerSingleton.getInstance();
    RemoteViews remoteViews;
    private BroadcastReceiver receiver;

    @Override
    public void onCreate() {
        Log.d("PlayerService", "onCreate called");
        super.onCreate();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Objects.equals(intent.getAction(), "PLAYBACK_STATE_CHANGED")) {
                    // Aggiorna l'interfaccia grafica del fragment
                    updateNotification();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("PLAYBACK_STATE_CHANGED");
        getApplicationContext().registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getAction() != null) {
                String action = intent.getStringExtra("action");
                if (action != null) {
                    handleAction(action);
                }
            } else {
                playerSingleton.start(getApplicationContext());
                startForeground(NOTIFICATION_ID, createNotification());
            }
        }

        return START_NOT_STICKY;
    }


    private void handleAction(String action) {
        switch (action) {
            case "PLAY_PAUSE":
                playerSingleton.playAndPause(getApplicationContext());
                updateNotification();
                break;
            case "NEXT":
                playerSingleton.next(getApplicationContext());
                updateNotification();
                break;
            case "PREVIOUS":
                playerSingleton.previous(getApplicationContext());
                updateNotification();
                break;
            default:
                break;
        }
    }

    private Notification createNotification() {
        // Creazione del canale di notifica
        createNotificationChannel();

        // Intent per aprire l'attività del player
        PendingIntent playerPendingIntent = createPlayerPendingIntent();

        // RemoteViews personalizzate
        RemoteViews remoteViews = createRemoteViews();

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(playerPendingIntent)
                .setCustomBigContentView(remoteViews)
//                .setCustomContentView(remoteViews)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setColor(ContextCompat.getColor(this, R.color.dark_purple))
                .setColorized(true)
                .setOngoing(true)
                .setSilent(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.FLAG_INSISTENT)
                .build();
    }

    public void updateNotification() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, createNotification());
    }

    // Metodo per la creazione del canale di notifica
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Music Player", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    // Metodo per creare il PendingIntent per l'attività del player
    private PendingIntent createPlayerPendingIntent() {
        Intent playerIntent = new Intent(this, MainActivity.class);
        playerIntent.putExtra("openPlayerFragment", true);
        playerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this, 0, playerIntent, PendingIntent.FLAG_IMMUTABLE);
    }


    // Metodo per creare il RemoteViews personalizzato
    private RemoteViews createRemoteViews() {
        remoteViews = new RemoteViews(getPackageName(), R.layout.player_notification);
        updateRemoteViews();

        // Intent e PendingIntent per le azioni di riproduzione
        remoteViews.setOnClickPendingIntent(R.id.play, createActionPendingIntent("PLAY_PAUSE"));
        remoteViews.setOnClickPendingIntent(R.id.previous, createActionPendingIntent("PREVIOUS"));
        remoteViews.setOnClickPendingIntent(R.id.next, createActionPendingIntent("NEXT"));

        return remoteViews;
    }

    public void updateRemoteViews() {
        Song song = playerSingleton.getSong();
        Bitmap coverBitmap = BitmapFactory.decodeResource(getResources(), song.getImage());

        // Decodifica delle risorse vettoriali con VectorDrawableCompat
        VectorDrawableCompat playDrawable = VectorDrawableCompat.create(getResources(), R.drawable.baseline_play_circle_24, null);
        VectorDrawableCompat pauseDrawable = VectorDrawableCompat.create(getResources(), R.drawable.baseline_pause_circle_filled_24, null);

        remoteViews.setTextViewText(R.id.tv_song_name, song.getTitle());
        remoteViews.setTextViewText(R.id.tv_artist, song.getArtists().get(0).getName());
        remoteViews.setImageViewBitmap(R.id.tv_song_image, coverBitmap);

        if (playerSingleton.isPlaying()) {
            // Imposta l'icona di pausa se la riproduzione è in corso
            remoteViews.setImageViewBitmap(R.id.play, convertToBitmap(pauseDrawable));
        } else {
            // Altrimenti, imposta l'icona di play
            remoteViews.setImageViewBitmap(R.id.play, convertToBitmap(playDrawable));
        }
    }

    // Metodo per convertire VectorDrawableCompat in Bitmap
    private Bitmap convertToBitmap(VectorDrawableCompat vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    // Metodo per creare il PendingIntent per le azioni di riproduzione
    private PendingIntent createActionPendingIntent(String action) {
        Intent intent = new Intent(this, PlayerService.class);
        intent.setAction(action);
        intent.putExtra("action", action);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playerSingleton.releaseMediaPlayer();
        getApplicationContext().unregisterReceiver(receiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}

