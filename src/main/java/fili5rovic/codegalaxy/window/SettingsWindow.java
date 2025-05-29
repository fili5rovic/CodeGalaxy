package fili5rovic.codegalaxy.window;

import fili5rovic.codegalaxy.Main;
import fili5rovic.codegalaxy.settings.IDESettings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.Objects;

public class SettingsWindow extends Window {

    @Override
    public void init(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("settings.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/settings-dark.css")).toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Settings");

            stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/fili5rovic/codegalaxy/png/app/codeGalaxy.png"))));

            this.stage = stage;

            this.stage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
                if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                    stage.hide();
                }
            });

            this.stage.setOnShowing(_ -> IDESettings.copySettingsToTemp());


        } catch (Exception e) {
            System.out.println("Error loading settings window: " + e.getMessage());
        }
    }
}
