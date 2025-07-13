package fili5rovic.codegalaxy.util;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Random;

public class AnimUtil {
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

        tab.setOnSelectionChanged(_ -> {
            if (!tab.isSelected()) {
                timeline.stop();
                selectedTab.setStyle("");
            } else {
                timeline.play();
            }
        });
    }

    public static void commitBtnNeon(Button button) {
        if (button == null) {
            System.out.println("Button is null, cannot animate.");
            return;
        }

        button.setOnMouseEntered(_ -> {
            if(!button.isDisabled())
                animateNodeNeon(button);
        });
        button.setOnMouseExited(_ -> stopNeonAnimation(button));
    }

    public static void animateNodeNeon(Node node) {
        if (node == null) {
            System.out.println("Node is null, cannot animate.");
            return;
        }

        // Create neon glow effect
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#8856c5"));
        glow.setRadius(8);
        glow.setSpread(0.6);
        node.setEffect(glow);

        // Purple matrix colors
        String[] matrixColors = {
                "#8856c5", "#a066d9", "#7c4fb8", "#b176e8", "#9960d1",
                "#ff88ff", "#e678e6", "#cc55cc", "#f099f0", "#d966d9"
        };

        Timeline timeline = new Timeline();
        Random random = new Random();

        // Create rapid matrix-style flashing effect
        for (int i = 0; i < 30; i++) {
            double time = i * 0.12;
            String color = matrixColors[random.nextInt(matrixColors.length)];

            // Random intensity values
            double glowRadius = 5 + random.nextDouble() * 15;
            double backgroundOpacity = 0.05 + random.nextDouble() * 0.15;
            int borderWidth = 1 + random.nextInt(3);

            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(time),
                            new KeyValue(node.styleProperty(),
                                    String.format("-fx-border-color: %s; -fx-border-width: %d; " +
                                                    "-fx-background-color: rgba(136, 86, 197, %.2f);",
                                            color, borderWidth, backgroundOpacity)),
                            new KeyValue(glow.radiusProperty(), glowRadius),
                            new KeyValue(glow.colorProperty(), Color.web(color))
                    )
            );
        }

        // Add some random "glitch" frames for extra matrix effect
        for (int i = 0; i < 5; i++) {
            double glitchTime = random.nextDouble() * 3.6; // Random time within cycle
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(glitchTime),
                            new KeyValue(node.styleProperty(),
                                    "-fx-border-color: #ffffff; -fx-border-width: 3; " +
                                            "-fx-background-color: rgba(255, 255, 255, 0.3);"),
                            new KeyValue(glow.radiusProperty(), 25),
                            new KeyValue(glow.colorProperty(), Color.WHITE)
                    )
            );
        }

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Store timeline reference for cleanup
        node.getProperties().put("neonTimeline", timeline);
    }

    public static void stopNeonAnimation(Node node) {
        if (node == null) return;

        Timeline timeline = (Timeline) node.getProperties().get("neonTimeline");
        if (timeline != null) {
            timeline.stop();
            node.getProperties().remove("neonTimeline");
        }

        node.setStyle("");
        node.setEffect(null);
    }
}