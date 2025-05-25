package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.lsp.LSP;
import fili5rovic.codegalaxy.util.SVGUtil;
import fili5rovic.codegalaxy.window.Window;
import javafx.event.ActionEvent;
import javafx.scene.control.SplitPane;

public class ButtonManager {

    private static final DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);

    private static double previousSliderValue = 0.2;

    public static void initialize() {
        buttonIcons();
        buttonActions();
    }

    private static void buttonActions() {
        controller.getShowHierarchyBtn().setOnAction(ButtonManager::showHierarchy);
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

    private static void showHierarchy(ActionEvent actionEvent) {
        SplitPane.Divider divider = controller.getMainSplitPane().getDividers().getFirst();
        if (divider.getPosition() < 0.01) {
            divider.setPosition(previousSliderValue);
        } else {
            previousSliderValue = divider.getPosition();
            divider.setPosition(0);
        }
    }


}
