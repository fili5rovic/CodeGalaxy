package fili5rovic.codegalaxy.util;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class TabColorAnimator {
    public static void animateTabBorder(Tab tab) {
        Node selectedTab = tab.getTabPane().lookup(".tab:selected");

        if (selectedTab == null) {
            System.out.println("Selected tab not found.");
            return;
        }

        ObjectProperty<Color> borderColor = new SimpleObjectProperty<>(Color.web("#8856c5"));

        borderColor.addListener((_, _, newColor) -> {
            String colorHex = String.format("#%02X%02X%02X",
                    (int)(newColor.getRed() * 255),
                    (int)(newColor.getGreen() * 255),
                    (int)(newColor.getBlue() * 255));
            selectedTab.setStyle("-fx-border-color: " + colorHex + "; -fx-border-width: 0 0 2 0;");
        });

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(borderColor, Color.web("#8856c5"))
                ),
                new KeyFrame(Duration.seconds(2),
                        new KeyValue(borderColor, Color.web("#ff88ff"))
                ),
                new KeyFrame(Duration.seconds(4),
                        new KeyValue(borderColor, Color.web("#8856c5"))
                )
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        tab.setOnSelectionChanged(event -> {
            if (!tab.isSelected()) {
                timeline.stop();
                selectedTab.setStyle("");
            } else {
                timeline.play();
            }
        });
    }
}