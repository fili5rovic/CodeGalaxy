package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.console.ConsoleArea;
import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.window.Window;

public class ConsoleHelper {
    private static DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);


    public static void initialize() {
        ConsoleArea consoleArea = new ConsoleArea();
        controller.getConsolePane().setCenter(consoleArea);



    }

}
