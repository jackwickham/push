package net.jackw.push.data.device;

public class DeviceDetails {

    private final String deviceName;
    private final String deviceId;

    public DeviceDetails(String deviceName, String deviceId) {
        this.deviceName = deviceName;
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
