package fili5rovic.codegalaxy.notification;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.window.Window;
import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;

public class NotificationPopup extends Popup {

    private final Label title;

    private final Label message;

    private static final DashboardController controller = ((DashboardController) Window.getController(Window.WINDOW_DASHBOARD));

    public NotificationPopup() {
        super();
        setAutoHide(true);
        setHideOnEscape(true);

        this.title = new Label();
        this.title.getStyleClass().add("notification-title");

        this.message = new Label();
        this.message.getStyleClass().add("notification-message");

        VBox root = new VBox(5);
        root.getStyleClass().add("notification-popup");
        root.getChildren().addAll(title, message);

        getContent().add(root);
    }

    public void show(String titleText, String messageText) {
        title.setText(titleText);
        message.setText(messageText);

        javafx.stage.Window owner = controller.getMainSplitPane().getScene().getWindow();

        double popupWidth = 300;
        double popupHeight = 80;

        double x = owner.getX() + owner.getWidth() - popupWidth - 20;
        double y = owner.getY() + owner.getHeight() - popupHeight - 40;

        show(owner, x, y);

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(_ -> hide());
        delay.play();
    }

}
