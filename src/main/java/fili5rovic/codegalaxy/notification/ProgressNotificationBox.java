package fili5rovic.codegalaxy.notification;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ProgressNotificationBox extends VBox {

    private final Label titleLabel;
    private final Label messageLabel;
    private final ProgressBar progressBar;
    private final Button cancelButton;
    private final Button closeButton;
    private final Task<?> task;
    private PauseTransition autoCloseTimer;

    public ProgressNotificationBox(String titleText, String messageText, Task<?> task) {
        this.task = task;

        setSpacing(8);
        setPadding(new Insets(12));
        getStyleClass().add("progress-notification-popup");
        setMaxWidth(350);
        setMinWidth(300);

        // Title and close button row - MODIFIED
        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        titleLabel = new Label(titleText);
        titleLabel.getStyleClass().add("notification-title");

        closeButton = new Button();
        closeButton.setText("X");
        closeButton.getStyleClass().clear();
        closeButton.getStyleClass().add("notification-close-btn");
        closeButton.setOnAction(_ -> closeNotification());

        // Give the title label all available space, pushing close button to the right
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        titleRow.getChildren().addAll(titleLabel, closeButton);

        // Message
        messageLabel = new Label(messageText);
        messageLabel.getStyleClass().add("notification-message");
        messageLabel.setWrapText(true);

        // Progress bar
        progressBar = new ProgressBar();
        progressBar.setPrefWidth(Double.MAX_VALUE);
        progressBar.getStyleClass().add("notification-progress");

        // Cancel button
        cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("notification-cancel-btn");
        cancelButton.setOnAction(_ -> cancelTask());

        HBox buttonRow = new HBox();
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        buttonRow.getChildren().add(cancelButton);

        getChildren().addAll(titleRow, messageLabel, progressBar, buttonRow);

        setupTaskBindings();
        playFadeInAnimation();
    }

    private void setupTaskBindings() {
        if (task != null) {
            // Bind progress and message
            progressBar.progressProperty().bind(task.progressProperty());
            messageLabel.textProperty().bind(task.messageProperty());

            // Handle task completion
            task.setOnSucceeded(_ -> onTaskCompleted("Completed successfully!", true));
            task.setOnFailed(_ -> onTaskCompleted("Failed: " + getErrorMessage(), false));
            task.setOnCancelled(_ -> onTaskCompleted("Cancelled", false));
        }
    }

    private String getErrorMessage() {
        if (task != null && task.getException() != null) {
            String message = task.getException().getMessage();
            return message != null ? message : "Unknown error";
        }
        return "Unknown error";
    }

    private void onTaskCompleted(String finalMessage, boolean success) {
        messageLabel.textProperty().unbind();
        messageLabel.setText(finalMessage);

        progressBar.setVisible(false);
        progressBar.setManaged(false);
        cancelButton.setVisible(false);
        cancelButton.setManaged(false);

        getStyleClass().removeAll("progress-notification-popup");
        getStyleClass().add(success ? "notification-success" : "notification-error");

        autosize();

        scheduleAutoClose();
    }

    private void cancelTask() {
        System.out.println("Task cancelled");
        if (task != null && !task.isDone()) {
            task.cancel();
        }
    }

    private void scheduleAutoClose() {
        autoCloseTimer = new PauseTransition(Duration.seconds(4));
        autoCloseTimer.setOnFinished(_ -> closeNotification());
        autoCloseTimer.play();
    }

    private void closeNotification() {
        if (autoCloseTimer != null) {
            autoCloseTimer.stop();
        }

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), this);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(_ -> {
            if (getParent() instanceof VBox parent) {
                parent.getChildren().remove(this);
            }
        });
        fadeOut.play();
    }

    private void playFadeInAnimation() {
        setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
}