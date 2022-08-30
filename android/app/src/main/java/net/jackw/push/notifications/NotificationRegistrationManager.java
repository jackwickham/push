package net.jackw.push.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import net.jackw.push.R;
import net.jackw.push.UserManager;
import net.jackw.push.data.device.DeviceDetails;
import net.jackw.push.data.device.DeviceDetailsRepository;
import net.jackw.push.data.registrations.NotificationRegistration;
import net.jackw.push.data.registrations.NotificationRegistrationRepository;

public class NotificationRegistrationManager {

    private static final String TAG = NotificationRegistrationManager.class.getName();

    private static final NotificationRegistrationManager instance = new NotificationRegistrationManager();

    public NotificationRegistrationManager() {}

    public static NotificationRegistrationManager getInstance() {
        return instance;
    }

    public void updateStoredToken(Context context) {
        String userId = UserManager.getInstance().getUserId();
        if (userId == null) {
            return;
        }
        DeviceDetails deviceDetails = DeviceDetailsRepository.getInstance().getDeviceDetails(context);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "Failed to fetch FCM registration token", task.getException());
                return;
            }

            String token = task.getResult();
            Log.d(TAG, "Notification token updated: " + token);
            NotificationRegistrationRepository.getInstance().putRegistration(
                    userId,
                    deviceDetails.getDeviceId(),
                    new NotificationRegistration(deviceDetails.getDeviceName(), token));
        });
    }

    public void setUpNotificationChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(
                context.getString(R.string.default_notification_channel_id),
                context.getString(R.string.default_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
