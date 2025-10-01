package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.controller.Controllers;
import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.window.Window;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;

public class SplitPaneManager {

    private static double lastKnownLeftPaneWidth = -1;
    private static double lastKnownDividerPosition = 0.25;

    private static boolean windowResizing = false;

    public static void setupLockPositions() {
        SplitPane mainSplitPane = Controllers.dashboardController().getMainSplitPane();

        try {
            lastKnownLeftPaneWidth = Double.parseDouble(IDESettings.getRecentInstance().get("lastKnownLeftPaneWidth"));
            lastKnownDividerPosition = Double.parseDouble(IDESettings.getRecentInstance().get("lastKnownDividerPosition"));
        } catch (NumberFormatException | NullPointerException e) {
            lastKnownLeftPaneWidth = -1;
        }

        ChangeListener<Number> widthListener = (_, _, newWidth) -> {
            if (newWidth.doubleValue() > 0 && lastKnownLeftPaneWidth > 0) {
                mainSplitPane.setDividerPosition(0, lastKnownLeftPaneWidth / newWidth.doubleValue());
            }
        };

        ChangeListener<Number> positionListener = (_, _, newPos) -> {
            if (!windowResizing && mainSplitPane.getItems().getFirst().isVisible()) {
                double currentWidth = mainSplitPane.getWidth();
                if (currentWidth > 0) {
                    double width = newPos.doubleValue() * currentWidth;
                    double pos = newPos.doubleValue();
                    if (width > 0 && pos > 0) {
                        lastKnownLeftPaneWidth = width;
                        lastKnownDividerPosition = pos;
                        IDESettings.getRecentInstance().set("lastKnownLeftPaneWidth", String.valueOf(lastKnownLeftPaneWidth));
                        IDESettings.getRecentInstance().set("lastKnownDividerPosition", String.valueOf(lastKnownDividerPosition));
                    }
                }
            }
        };

        mainSplitPane.widthProperty().addListener(widthListener);
        mainSplitPane.getDividers().getFirst().positionProperty().addListener(positionListener);

        Platform.runLater(() -> {
            double initialWidth = mainSplitPane.getWidth();
            if (initialWidth > 0) {
                if (lastKnownLeftPaneWidth > 0) {
                    mainSplitPane.setDividerPosition(0, lastKnownLeftPaneWidth / initialWidth);
                } else {
                    lastKnownLeftPaneWidth = mainSplitPane.getDividerPositions()[0] * initialWidth;
                }
            }
        });

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
            if (left.isVisible()) {
                lastKnownDividerPosition = divider.getPosition();
            }
            divider.setPosition(0);
            left.setVisible(false);
        } else {
            divider.setPosition(lastKnownDividerPosition);
            left.setVisible(true);
        }
    }


}