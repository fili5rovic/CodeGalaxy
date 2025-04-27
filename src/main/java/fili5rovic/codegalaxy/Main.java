package fili5rovic.codegalaxy;

import fili5rovic.codegalaxy.window.DashboardWindow;
import fili5rovic.codegalaxy.window.SettingsWindow;
import fili5rovic.codegalaxy.window.Window;
import fili5rovic.codegalaxy.window.WindowHelper;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Window.setWindowAt(Window.WINDOW_DASHBOARD, new DashboardWindow());
        Window.setWindowAt(Window.SETTINGS, new SettingsWindow());

        Window.initAllWindows();
        WindowHelper.showWindow(Window.WINDOW_DASHBOARD);
    }

    public static void main(String[] args) {
        launch();
    }
}