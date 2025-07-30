package fili5rovic.codegalaxy.window;

import fili5rovic.codegalaxy.controller.DashboardController;

public class DashboardWindow extends Window {

    public DashboardWindow() {
        this.title = "CodeGalaxy";
        this.fxmlName = "ide";
//        this.extended = true;
    }

    @Override
    public String[] cssFileNames() {
        return new String[]{"main", "codegalaxy"};
    }

    @Override
    public void listeners() {
        this.stage.setOnCloseRequest(e -> ((DashboardController) Window.getController(Window.WINDOW_DASHBOARD)).onAppClose(e));
    }


}