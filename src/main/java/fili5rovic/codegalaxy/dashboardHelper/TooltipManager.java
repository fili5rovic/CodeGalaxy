package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.window.Window;
import javafx.scene.control.Tooltip;

public class TooltipManager {

    private static final DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);

    public static void init() {
        controller.getShowErrorsBtn().setTooltip(makeFastTooltip("Problems"));
        controller.getShowRunBtn().setTooltip(makeFastTooltip("Run"));
    }

    private static Tooltip makeFastTooltip(String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(javafx.util.Duration.millis(200));
        tooltip.setAutoHide(true);
        return tooltip;
    }

}
