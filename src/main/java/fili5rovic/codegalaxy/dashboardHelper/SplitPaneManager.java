package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.window.Window;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;

public class SplitPaneManager {
    private static final DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);


    public static void setupLockPositions() {
        Platform.runLater(() -> {
            SplitPane mainSplitPane = controller.getMainSplitPane();
            System.out.println(mainSplitPane.getWidth());
            mainSplitPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                ObservableList<SplitPane.Divider> dividers = mainSplitPane.getDividers();

                double ratio = 966.0 / newValue.doubleValue();
                dividers.get(0).setPosition(0.2 * ratio);
                dividers.get(1).setPosition(0.97 * ratio);
            });



        });
    }
}
