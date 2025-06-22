package fili5rovic.codegalaxy.notification;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class NotificationBox extends VBox {

    public NotificationBox(String titleText, String messageText, Runnable onHide) {
        setSpacing(5);
        getStyleClass().add("notification-popup");

        Label title = new Label(titleText);
        title.getStyleClass().add("notification-title");

        Label message = new Label(messageText);
        message.getStyleClass().add("notification-message");

        getChildren().addAll(title, message);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), this);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(ev -> onHide.run());
            fadeOut.play();
        });
        delay.play();
    }
}
