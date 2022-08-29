package net.jackw.push.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseError;

public interface EventListener<T> {
    void onDataChange(@Nullable T value);
    void onError(@NonNull DatabaseError error);
}
