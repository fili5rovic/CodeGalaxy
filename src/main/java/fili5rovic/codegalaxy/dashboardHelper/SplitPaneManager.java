package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.window.Window;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;

import java.util.Comparator;
import java.util.List;

public class SplitPaneManager {
    private static final DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);

    private static double prevHierarchyDividerValue = 0;

    private static boolean windowResizing = false;

    public static void setupLockPositions() {
        prevHierarchyDividerValue = Double.parseDouble(IDESettings.getInstance().get("prevHierarchyDividerValue"));

        SplitPane mainSplitPane = controller.getMainSplitPane();
        mainSplitPane.getDividers().getFirst().setPosition(prevHierarchyDividerValue);

        mainSplitPane.widthProperty().addListener((_, _, newValue) -> {
            ObservableList<SplitPane.Divider> dividers = mainSplitPane.getDividers();
            double ratio = 966.0 / newValue.doubleValue();
            dividers.getFirst().setPosition(prevHierarchyDividerValue * ratio);
        });

        Platform.runLater(() -> mainSplitPane.getScene().addPreLayoutPulseListener(() -> {
            windowResizing = true;
            Platform.runLater(() -> {
                windowResizing = false;
            });
        }));

        mainSplitPane.getDividers().getFirst().positionProperty().addListener((_, _, newValue) -> {
            if (!windowResizing && newValue.doubleValue() > 0.01) {
                prevHierarchyDividerValue = newValue.doubleValue();
                IDESettings.getInstance().set("prevHierarchyDividerValue", String.valueOf(prevHierarchyDividerValue));
            }
        });

    }

    public static void showLeftPanel(boolean selected) {
        SplitPane mainSplitPane = controller.getMainSplitPane();
        SplitPane.Divider divider = mainSplitPane.getDividers().getFirst();
        Node left = mainSplitPane.getItems().getFirst();
        if (selected) {
            prevHierarchyDividerValue = divider.getPosition();
            divider.setPosition(0);
            left.setVisible(false);
            enableFirstDivider(mainSplitPane, false);
        } else {
            divider.setPosition(prevHierarchyDividerValue);
            left.setVisible(true);
            enableFirstDivider(mainSplitPane, true);
        }
    }

    private static void enableFirstDivider(SplitPane splitPane, boolean enable) {
        Platform.runLater(() -> {
            List<Node> dividers = splitPane.lookupAll(".split-pane-divider")
                    .stream()
                    .sorted(Comparator.comparingDouble(d -> ((Region) d).getLayoutX())) // sort left to right
                    .toList();
            if (!dividers.isEmpty()) {
                Node firstDivider = dividers.getFirst();
                firstDivider.setMouseTransparent(!enable);
            }
        });
    }
}
