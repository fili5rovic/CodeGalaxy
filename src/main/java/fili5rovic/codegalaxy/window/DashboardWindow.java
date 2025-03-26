package fili5rovic.codegalaxy.window;

import fili5rovic.codegalaxy.Main;
import fili5rovic.codegalaxy.controller.DashboardController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DashboardWindow extends Window {
    @Override
    public void init(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ide.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(Main.class.getResource("/fili5rovic/codegalaxy/main.css").toExternalForm());
            scene.getStylesheets().add(Main.class.getResource("/fili5rovic/codegalaxy/codegalaxy.css").toExternalForm());
            stage.setTitle("Code Galaxy");
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNIFIED);
            stage.show();


            this.stage = stage;

            this.stage.setOnCloseRequest(e-> {
                ((DashboardController) Window.getController(Window.WINDOW_DASHBOARD)).onAppClose(e);
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}