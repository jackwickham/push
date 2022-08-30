package net.jackw.push.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import net.jackw.push.R;
import net.jackw.push.notifications.NotificationRegistrationManager;

import java.util.concurrent.atomic.AtomicInteger;

public class PushFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "PushFirebaseMessagingService";
    private static final String ACTION_COPY = "net.jackw.push.ACTION_COPY";
    private static final String EXTRA_PAYLOAD = "payload";
    private static final String EXTRA_NOTIFICATION_ID = "notificationId";

    private final AtomicInteger nextNotificationId = new AtomicInteger(0);

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("data", intent.getStringExtra(EXTRA_PAYLOAD));
            clipboardManager.setPrimaryClip(clipData);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1));

            Toast.makeText(context, getText(R.string.copied), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Log.d(TAG, "received message from: " + message.getFrom());

        String payload = message.getData().get("payload");
        if (payload == null) {
            Log.w(TAG, "Empty notification payload");
            return;
        }

        int notificationId = nextNotificationId.getAndIncrement();

        // Make sure the receiver is still registered
        registerReceiver(broadcastReceiver, new IntentFilter(ACTION_COPY));
        Intent copyIntent = new Intent(ACTION_COPY);
        copyIntent.putExtra(EXTRA_PAYLOAD, payload);
        copyIntent.putExtra(EXTRA_NOTIFICATION_ID, notificationId);
        PendingIntent pendingCopyIntent = PendingIntent.getBroadcast(this, this.nextNotificationId.incrementAndGet(), copyIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(payload)
                .setChannelId(getString(R.string.default_notification_channel_id))
                .setContentIntent(pendingCopyIntent)
                .setSmallIcon(R.drawable.send)
                .addAction(R.drawable.copy, getString(R.string.copy), pendingCopyIntent);

        if ((payload.startsWith("https://") || payload.startsWith("http://")) && URLUtil.isValidUrl(payload)) {
            Intent openIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(payload));
            PendingIntent pendingOpenIntent =
                    PendingIntent.getActivity(this, notificationId, openIntent, PendingIntent.FLAG_IMMUTABLE);
            notificationBuilder.addAction(R.drawable.open, getString(R.string.open), pendingOpenIntent);
        }

        NotificationManagerCompat.from(this).notify(notificationId, notificationBuilder.build());
    }

    @Override
    public void onNewToken(@NonNull String _token) {
        NotificationRegistrationManager.getInstance().updateStoredToken(this);
    }
}
