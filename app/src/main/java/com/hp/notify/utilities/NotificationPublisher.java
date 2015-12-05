package com.hp.notify.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

/**
 * Created by HP Labs on 11/25/2015.
 */
public class NotificationPublisher extends BroadcastReceiver {

    // Constants
    public static final String NOTIFICATION_ID = "notification-id";
    public static final String NOTIFICATION = "notification";

    // Instance variables
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);

        // Vibrate the mobile phone
        // Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // vibrator.vibrate(500);


    }
}
