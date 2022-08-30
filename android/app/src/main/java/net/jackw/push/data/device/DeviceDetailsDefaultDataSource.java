package net.jackw.push.data.device;

import android.content.Context;
import android.provider.Settings;

public class DeviceDetailsDefaultDataSource {

    public String getDeviceName(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), "bluetooth_name");
    }
}
