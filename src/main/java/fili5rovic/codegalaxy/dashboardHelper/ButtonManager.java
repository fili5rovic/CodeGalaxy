package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.util.SVGUtil;
import fili5rovic.codegalaxy.window.Window;
import javafx.event.ActionEvent;

public class ButtonManager {

    private static final DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);

    public static void initialize() {
        buttonIcons();
        buttonActions();
    }

    private static void buttonActions() {
        controller.getShowHierarchyBtn().setOnAction(ButtonManager::showHierarchy);
    }

    private static void buttonIcons() {
        controller.getShowHierarchyBtn().setGraphic(SVGUtil.getUI("openProject", 16, 16));
    }

    private static void showHierarchy(ActionEvent actionEvent) {
    }


}
