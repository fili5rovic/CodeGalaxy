package fili5rovic.codegalaxy.notification;

public class Notification {

    private static NotificationPopup popup;

    public static void show(String title, String message) {
        if (popup == null)
            popup = new NotificationPopup();

        popup.show(title, message);
    }
}
