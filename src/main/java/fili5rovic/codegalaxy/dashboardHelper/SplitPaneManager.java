package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.controller.Controllers;
import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.window.Window;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;

import java.util.Comparator;
import java.util.List;

public class SplitPaneManager {

    // Store the last known absolute width of the left pane
    private static double lastKnownLeftPaneWidth = -1;
    // Store the last known divider position (ratio) to handle hiding/showing the panel
    private static double lastKnownDividerPosition = 0.25; // A sensible default

    private static boolean windowResizing = false;

    public static void setupLockPositions() {
        SplitPane mainSplitPane = Controllers.dashboardController().getMainSplitPane();

        // Load saved values, with defaults if not present
        try {
            lastKnownLeftPaneWidth = Double.parseDouble(IDESettings.getRecentInstance().get("lastKnownLeftPaneWidth"));
            lastKnownDividerPosition = Double.parseDouble(IDESettings.getRecentInstance().get("lastKnownDividerPosition"));
        } catch (NumberFormatException | NullPointerException e) {
            // Could not parse settings, will use defaults or calculate on first layout pass
            lastKnownLeftPaneWidth = -1;
        }

        // Listener to recalculate divider position when the SplitPane width changes
        ChangeListener<Number> widthListener = (obs, oldWidth, newWidth) -> {
            if (newWidth.doubleValue() > 0 && lastKnownLeftPaneWidth > 0) {
                // We have a target width, calculate the new ratio to maintain it
                mainSplitPane.setDividerPosition(0, lastKnownLeftPaneWidth / newWidth.doubleValue());
            }
        };

        // Listener to update the saved width when the user manually drags the divider
        ChangeListener<Number> positionListener = (obs, oldPos, newPos) -> {
            // Only update if the change is from a user drag (not a window resize) and the pane is visible
            if (!windowResizing && mainSplitPane.getItems().getFirst().isVisible()) {
                double currentWidth = mainSplitPane.getWidth();
                if (currentWidth > 0) {
                    lastKnownLeftPaneWidth = newPos.doubleValue() * currentWidth;
                    lastKnownDividerPosition = newPos.doubleValue();
                    IDESettings.getRecentInstance().set("lastKnownLeftPaneWidth", String.valueOf(lastKnownLeftPaneWidth));
                    IDESettings.getRecentInstance().set("lastKnownDividerPosition", String.valueOf(lastKnownDividerPosition));
                }
            }
        };

        mainSplitPane.widthProperty().addListener(widthListener);
        mainSplitPane.getDividers().getFirst().positionProperty().addListener(positionListener);

        // A one-time setup to initialize the divider position correctly after the first layout pass
        Platform.runLater(() -> {
            double initialWidth = mainSplitPane.getWidth();
            if (initialWidth > 0) {
                if (lastKnownLeftPaneWidth > 0) {
                    // Use saved width to set initial position
                    mainSplitPane.setDividerPosition(0, lastKnownLeftPaneWidth / initialWidth);
                } else {
                    // First run or no saved width, calculate it from the default position
                    lastKnownLeftPaneWidth = mainSplitPane.getDividerPositions()[0] * initialWidth;
                }
            }
        });


        // Flag to distinguish between user drags and programmatic resizing
        Platform.runLater(() -> Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage().getScene().addPreLayoutPulseListener(() -> {
            windowResizing = true;
            Platform.runLater(() -> windowResizing = false);
        }));
    }

    public static void showLeftPanel(boolean hide) {
        SplitPane mainSplitPane = Controllers.dashboardController().getMainSplitPane();
        SplitPane.Divider divider = mainSplitPane.getDividers().getFirst();
        Node left = mainSplitPane.getItems().getFirst();
        if (hide) {
            // Store the current position before hiding
            if (left.isVisible()) {
                lastKnownDividerPosition = divider.getPosition();
            }
            divider.setPosition(0);
            left.setVisible(false);
            enableFirstDivider(mainSplitPane, false);
        } else {
            // Restore to the last known position
            divider.setPosition(lastKnownDividerPosition);
            left.setVisible(true);
            enableFirstDivider(mainSplitPane, true);
        }
    }

    private static void enableFirstDivider(SplitPane splitPane, boolean enable) {
        Platform.runLater(() -> {
            List<Node> dividers = splitPane.lookupAll(".split-pane-divider")
                    .stream()
                    .sorted(Comparator.comparingDouble(d -> ((Region) d).getLayoutX()))
                    .toList();
            if (!dividers.isEmpty()) {
                dividers.getFirst().setMouseTransparent(!enable);
            }
        });
    }
}