package com.soundlab.app.utils;

import android.os.Handler;
import java.util.concurrent.TimeUnit;

public class Debouncer {
    private final Handler handler;
    private Runnable runnable;

    public Debouncer() {
        this.handler = new Handler();
    }

    public void debounce(final Runnable action, long delayMillis) {
        // Cancella l'ultimo Runnable postato
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
        runnable = new Runnable() {
            @Override
            public void run() {
                action.run();
            }
        };
        // Pianifica il nuovo Runnable
        handler.postDelayed(runnable, delayMillis);
    }
}
