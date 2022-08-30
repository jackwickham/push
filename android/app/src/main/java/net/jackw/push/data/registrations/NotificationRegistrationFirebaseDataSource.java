package net.jackw.push.data.registrations;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.jackw.push.data.EventListener;

public class NotificationRegistrationFirebaseDataSource {

    private final FirebaseDatabase database;

    public NotificationRegistrationFirebaseDataSource() {
        database = FirebaseDatabase.getInstance("https://push-508bd-default-rtdb.europe-west1.firebasedatabase.app/");
    }

    public void getRegistrations(String userId, EventListener<Set<NotificationRegistration>> eventListener) {
        database.getReference()
                .child("users")
                .child(userId)
                .child("pushRegistrations")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, NotificationRegistration> registrations =
                                snapshot.getValue(new GenericTypeIndicator<Map<String, NotificationRegistration>>() {});
                        if (registrations == null) {
                            eventListener.onDataChange(Collections.emptySet());
                        } else {
                            eventListener.onDataChange(new HashSet<>(registrations.values()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        eventListener.onError(error);
                    }
                });
    }

    public void putRegistration(String userId, String deviceId, NotificationRegistration registration) {
        database.getReference()
                .child("users")
                .child(userId)
                .child("pushRegistrations")
                .child(deviceId)
                .setValue(registration);
    }
}
