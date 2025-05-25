package fili5rovic.codegalaxy.errors;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.window.Window;

public class DisplayErrorsHandler {

    private static DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);



    public static void displayErrors() {
        controller.getConsoleTabPane().setVisible(false);
        controller.getErrorTabPane().setVisible(true);



    }
}
