package fili5rovic.codegalaxy.window;


import fili5rovic.codegalaxy.controller.ControllerBase;
import javafx.stage.Stage;

public abstract class Window {
    protected Stage stage;
    protected ControllerBase controller;
    public static final int WINDOWS = 2;
    public static final int WINDOW_DASHBOARD = 0;
    public static final int SETTINGS = 1;
    private static final Window[] windows = new Window[WINDOWS];

    public static void setWindowAt(int position, Window w) {
        windows[position] = w;
    }

    public static Window getWindowAt(int position) {
        return windows[position];
    }

    public static void initAllWindows() {
        for (int i = 0; i < WINDOWS; i++) {
            windows[i].init(new Stage());
        }
    }

    public abstract void init(Stage stage);

    //<editor-fold desc="Geteri i Seteri">
    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public static ControllerBase getController(int position) {
        return windows[position].controller;
    }

    public void setController(ControllerBase controller) {
        this.controller = controller;
    }
    //</editor-fold>

}

