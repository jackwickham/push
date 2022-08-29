package net.jackw.push.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import net.jackw.push.R;

public class Notifications {

    private final NotificationManager notificationManager;

    public Notifications(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public void init(Context context) {
        NotificationChannel channel = new NotificationChannel(
                context.getString(R.string.default_notification_channel_id),
                context.getString(R.string.default_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
