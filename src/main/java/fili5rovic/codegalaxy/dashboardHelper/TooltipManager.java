package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.controller.Controllers;
import javafx.scene.control.Tooltip;

public class TooltipManager {

    public static void init() {
        Controllers.dashboardController().getShowProblemsToggle().setTooltip(makeFastTooltip("Problems"));
        Controllers.dashboardController().getShowRunToggle().setTooltip(makeFastTooltip("Run"));
    }

    private static Tooltip makeFastTooltip(String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(javafx.util.Duration.millis(200));
        return tooltip;
    }

}
