package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.window.Window;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;

public class SplitPaneManager {
    private static final DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);

    private static double lastPositionFirstDivider = 0;

    private static boolean windowResizing = false;

    public static void setupLockPositions() {
        SplitPane mainSplitPane = controller.getMainSplitPane();

        Platform.runLater(() -> {
            mainSplitPane.widthProperty().addListener((_, _, newValue) -> {
                ObservableList<SplitPane.Divider> dividers = mainSplitPane.getDividers();
                double ratio = 966.0 / newValue.doubleValue();
                dividers.getFirst().setPosition(lastPositionFirstDivider * ratio);
            });

            mainSplitPane.getScene().addPreLayoutPulseListener(() -> {
                windowResizing = true;
                Platform.runLater(() -> {
                    windowResizing = false;
                });
            });

            mainSplitPane.getDividers().getFirst().positionProperty().addListener((_, _, newValue) -> {
                if (!windowResizing) {
                    lastPositionFirstDivider = newValue.doubleValue();
                }
            });


        });

    }
}
