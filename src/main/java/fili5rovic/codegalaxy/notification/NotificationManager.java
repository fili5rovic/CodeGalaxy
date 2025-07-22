package fili5rovic.codegalaxy.notification;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.window.Window;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.layout.VBox;

public class NotificationManager {

    private static VBox container;
    private static boolean initialized = false;

    private static void ensureInitialized() {
        if (!initialized) {
            DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);
            container = controller.getNotificationVBox();
            container.setPadding(new javafx.geometry.Insets(20, 0, 0, 0));
            initialized = true;
        }
    }

    // Original method for simple notifications
    public static void show(String title, String message) {
        ensureInitialized();
        Platform.runLater(() -> {
            NotificationBox notification = new NotificationBox(title, message);
            container.getChildren().addLast(notification);
        });
    }

    public static void showProgress(String title, String message, Task<?> task) {
        ensureInitialized();
        Platform.runLater(() -> {
            ProgressNotificationBox notification = new ProgressNotificationBox(title, message, task);
            container.getChildren().addLast(notification);
        });
    }
}