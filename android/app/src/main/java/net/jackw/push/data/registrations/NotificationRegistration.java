package net.jackw.push.data.registrations;

public class NotificationRegistration {
    private String deviceName;
    private String notificationToken;

    private NotificationRegistration() {
        // Deserialization constructor
    }

    public NotificationRegistration(String deviceName, String notificationToken) {
        this.deviceName = deviceName;
        this.notificationToken = notificationToken;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getNotificationToken() {
        return notificationToken;
    }
}
