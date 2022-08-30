package net.jackw.push.data.device;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import java.util.Optional;
import java.util.UUID;

public class DeviceDetailsStoreDataSource {

    private static final String PREF_STORE_KEY = "net.jackw.push.DEVICE_DETAILS";
    private static final String DEVICE_NAME = "device-name";
    private static final String DEVICE_ID = "device-id";

    public Optional<String> getDeviceName(Context context) {
        return Optional.ofNullable(getSharedPreferences(context).getString(DEVICE_NAME, null));
    }

    @NonNull
    public String getDeviceId(Context context) {
        String deviceId = getSharedPreferences(context).getString(DEVICE_ID, null);
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString();
            getSharedPreferences(context).edit().putString(DEVICE_ID, deviceId).apply();
        }
        return deviceId;
    }

    public void setDeviceName(Context context, String deviceName) {
        getSharedPreferences(context).edit().putString(DEVICE_NAME, deviceName).apply();
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_STORE_KEY, Context.MODE_PRIVATE);
    }
}
