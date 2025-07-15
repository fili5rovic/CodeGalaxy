package fili5rovic.codegalaxy.window;

import fili5rovic.codegalaxy.settings.IDESettings;
import javafx.scene.input.KeyEvent;

public class SettingsWindow extends Window {

    public SettingsWindow() {
        this.title = "Settings";
        this.fxmlName = "settings";
    }


    @Override
    public String[] cssFileNames() {
        return new String[]{"settings"};
    }

    @Override
    public void listeners() {
        this.stage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                stage.hide();
            }
        });
    }
}
