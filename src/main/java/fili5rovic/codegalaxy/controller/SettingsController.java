package fili5rovic.codegalaxy.controller;

import fili5rovic.codegalaxy.window.Window;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController extends ControllerBase {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Window.getWindowAt(Window.SETTINGS).setController(this);

        System.out.println("SettingsController initialized");
    }
}
