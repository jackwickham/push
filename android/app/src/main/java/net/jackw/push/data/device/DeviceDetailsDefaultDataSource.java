package net.jackw.push.data.device;

import android.content.Context;
import android.provider.Settings;

import com.google.firebase.installations.FirebaseInstallations;

import java.util.Optional;

public class DeviceDetailsDefaultDataSource {

    public String getDeviceName(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), "bluetooth_name");
    }
}
