package fili5rovic.codegalaxy.controller;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

public abstract class ControllerBase {
    /**
     * Initializes the controller after stage is not null and FXML is loaded.
     */
    public abstract void lateInitialize(Stage stage);
}

