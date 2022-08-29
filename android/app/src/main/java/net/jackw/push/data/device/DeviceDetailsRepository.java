package net.jackw.push.data.device;

import android.content.Context;

import androidx.annotation.Nullable;

public class DeviceDetailsRepository {

    @Nullable
    private static DeviceDetailsRepository instance;

    private final DeviceDetailsStoreDataSource storeDataSource;
    private final DeviceDetailsDefaultDataSource defaultDataSource;

    private DeviceDetailsRepository(
            DeviceDetailsStoreDataSource storeDataSource, DeviceDetailsDefaultDataSource defaultDataSource) {
        this.storeDataSource = storeDataSource;
        this.defaultDataSource = defaultDataSource;
    }

    public static DeviceDetailsRepository getInstance() {
        if (instance == null) {
            synchronized (DeviceDetailsRepository.class) {
                if (instance == null) {
                    instance = new DeviceDetailsRepository(new DeviceDetailsStoreDataSource(), new DeviceDetailsDefaultDataSource());
                }
            }
        }
        return instance;
    }

    public DeviceDetails getDeviceDetails(Context context) {
        String deviceName = storeDataSource.getDeviceName(context).orElseGet(() -> defaultDataSource.getDeviceName(context));
        String deviceId = storeDataSource.getDeviceId(context);
        return new DeviceDetails(deviceName, deviceId);
    }

    public void setDeviceName(Context context, String name) {
        storeDataSource.setDeviceName(context, name);
    }
}
