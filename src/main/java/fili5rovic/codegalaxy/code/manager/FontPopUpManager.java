package fili5rovic.codegalaxy.code.manager;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

/**
 * Responsible for popup that describes the current text font size
 */
public class FontPopUpManager extends Manager {
    private StackPane overlay;
    private Thread timeThread;
    private int sleepTime = 2000;


    public FontPopUpManager(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    public void init() {
        createOverlay();
        listeners();
    }

    private void listeners() {
        codeGalaxy.widthProperty().addListener(e -> {
            double layoutX = codeGalaxy.getWidth()/2;
            overlay.setLayoutX(layoutX);
        });
        codeGalaxy.heightProperty().addListener(e -> {
            double layoutY = codeGalaxy.getHeight() - 50;
            overlay.setLayoutY(layoutY);
        });
    }

    private void createOverlay() {
        overlay = new StackPane();
        overlay.setVisible(false);
        codeGalaxy.getChildren().add(overlay);
    }

    public void showMessage(String message) {
        Label messageText = new Label(message);
        messageText.setMinWidth(60);
        messageText.setFont(new Font("monospaced",18));
        messageText.setStyle("-fx-fill: #000000; -fx-background-color: rgba(107,148,159,0.49); -fx-padding: 10; -fx-background-radius: 15");

        overlay.getChildren().clear();
        overlay.getChildren().add(messageText);
        overlay.setVisible(true);

        if (timeThread != null && timeThread.isAlive()) {
            timeThread.interrupt();
            overlay.setVisible(true);
        }

        timeThread = new Thread(() -> {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                return;
            }
            Platform.runLater(() -> overlay.setVisible(false));
        });
        timeThread.start();
    }

}
