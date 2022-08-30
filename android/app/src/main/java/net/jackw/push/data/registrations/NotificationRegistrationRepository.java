package net.jackw.push.data.registrations;

import androidx.annotation.Nullable;
import java.util.Set;
import net.jackw.push.data.EventListener;

public class NotificationRegistrationRepository {

    @Nullable
    private static NotificationRegistrationRepository instance = null;

    private final NotificationRegistrationFirebaseDataSource firebaseDataSource;

    private NotificationRegistrationRepository(NotificationRegistrationFirebaseDataSource firebaseDataSource) {
        this.firebaseDataSource = firebaseDataSource;
    }

    public static NotificationRegistrationRepository getInstance() {
        if (instance == null) {
            synchronized (NotificationRegistrationRepository.class) {
                if (instance == null) {
                    instance = new NotificationRegistrationRepository(new NotificationRegistrationFirebaseDataSource());
                }
            }
        }
        return instance;
    }

    public void getRegistrations(String userId, EventListener<Set<NotificationRegistration>> eventListener) {
        firebaseDataSource.getRegistrations(userId, eventListener);
    }

    public void putRegistration(String userId, String deviceId, NotificationRegistration registration) {
        firebaseDataSource.putRegistration(userId, deviceId, registration);
    }
}
