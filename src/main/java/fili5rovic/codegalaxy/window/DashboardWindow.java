package fili5rovic.codegalaxy.window;

import fili5rovic.codegalaxy.Main;
import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.util.CSSUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class DashboardWindow extends Window {
    @Override
    public void init(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ide.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            CSSUtil.applyStylesheet(scene.getStylesheets(), "main");
            CSSUtil.applyStylesheet(scene.getStylesheets(), "codegalaxy");
            stage.setTitle("CodeGalaxy");
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNIFIED);

            stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/fili5rovic/codegalaxy/png/app/codeGalaxy.png"))));

            this.stage = stage;

            this.stage.setOnCloseRequest(e -> ((DashboardController) Window.getController(Window.WINDOW_DASHBOARD)).onAppClose(e));

        } catch (Exception e) {
            System.err.println("Error loading dashboard window: " + e.getMessage());
        }
    }
}