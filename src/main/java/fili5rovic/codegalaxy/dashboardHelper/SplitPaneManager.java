package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.window.Window;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;

public class SplitPaneManager {
    private static final DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);

    // When you want to add another pane to the right, make it a child of mainSplitPane
    public static void setupLockPositions() {
        Platform.runLater(() -> {
            SplitPane mainSplitPane = controller.getMainSplitPane();
            mainSplitPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                ObservableList<SplitPane.Divider> dividers = mainSplitPane.getDividers();

                double ratio = 966.0 / newValue.doubleValue();
                dividers.getFirst().setPosition(0.2 * ratio);
            });
        });
    }
}
