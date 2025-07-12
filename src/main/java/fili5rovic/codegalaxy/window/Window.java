package fili5rovic.codegalaxy.window;


import fili5rovic.codegalaxy.Main;
import fili5rovic.codegalaxy.controller.ControllerBase;
import fili5rovic.codegalaxy.util.CSSUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public abstract class Window {
    public static final int WINDOWS = 2;
    public static final int WINDOW_DASHBOARD = 0;
    public static final int SETTINGS = 1;
    private static final Window[] windows = new Window[WINDOWS];

    protected Stage stage;
    protected ControllerBase controller;


    protected String title = "TITLE";
    protected String fxmlName = "FXML_NAME";

    public static void setWindowAt(int position, Window w) {
        windows[position] = w;
    }

    public static Window getWindowAt(int position) {
        return windows[position];
    }

    public static void initAllWindows() {
        for (int i = 0; i < WINDOWS; i++) {
            Stage stage = new Stage();
            windows[i].init(stage);
        }
    }

    public String[] cssFileNames() {
        return new String[0];
    }

    public void init(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/" + this.fxmlName + ".fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            for (String cssFileName : cssFileNames()) {
                CSSUtil.applyStylesheet(scene.getStylesheets(), cssFileName);
            }
            stage.setScene(scene);
            stage.setTitle(this.title);

            stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/fili5rovic/codegalaxy/png/app/codeGalaxy.png"))));

            this.stage = stage;

            if(fxmlLoader.getController() instanceof ControllerBase baseController) {
                this.controller = baseController;
                this.controller.lateInitialize(stage);

            } else {
                throw new IllegalStateException("Controller must be an instance of ControllerBase");
            }

            listeners();

        } catch (Exception e) {
            System.err.println("Error loading window: " + e.getMessage());
        }
    }

    public void listeners() {}

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

