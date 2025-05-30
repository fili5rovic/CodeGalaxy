package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.util.SVGUtil;
import fili5rovic.codegalaxy.window.Window;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;

import java.util.Comparator;
import java.util.List;

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
        SplitPane mainSplitPane = controller.getMainSplitPane();
        SplitPane.Divider divider = mainSplitPane.getDividers().getFirst();
        if (divider.getPosition() < 0.01) {
            divider.setPosition(previousSliderValue);
            enableFirstDivider(mainSplitPane, true);
        } else {
            previousSliderValue = divider.getPosition();
            divider.setPosition(0);
            enableFirstDivider(mainSplitPane, false);
        }
    }

    private static void enableFirstDivider(SplitPane splitPane, boolean enable) {
        Platform.runLater(() -> {
            List<Node> dividers = splitPane.lookupAll(".split-pane-divider")
                    .stream()
                    .sorted(Comparator.comparingDouble(d -> ((Region) d).getLayoutX())) // sort left to right
                    .toList();
            if (!dividers.isEmpty()) {
                Node firstDivider = dividers.get(0);
                firstDivider.setMouseTransparent(!enable);
            }
        });
    }



}
