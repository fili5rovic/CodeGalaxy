package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.util.SVGUtil;
import fili5rovic.codegalaxy.window.Window;

public class ButtonManager {

    private static final DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);

    public static void initialize() {
        buttonIcons();
        buttonActions();
    }

    private static void buttonActions() {
        controller.getShowHierarchyBtn().setOnAction(_ -> SplitPaneManager.showHierarchy());
        controller.getShowRunBtn().setOnAction(_ -> {
            controller.getConsoleTabPane().setVisible(true);
            controller.getErrorTabPane().setVisible(false);
        });
        controller.getShowErrorsBtn().setOnAction(_ -> {
            controller.getConsoleTabPane().setVisible(false);
            controller.getErrorTabPane().setVisible(true);
        });
    }

    private static void buttonIcons() {
        controller.getShowHierarchyBtn().setGraphic(SVGUtil.getUI("expand", 16, 16));
        controller.getShowRunBtn().setGraphic(SVGUtil.getUI("runBtn", 16, 16));
        controller.getShowErrorsBtn().setGraphic(SVGUtil.getUI("error", 16, 16));
    }


}
