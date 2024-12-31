package fili5rovic.codegalaxy;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {
    @FXML
    private BorderPane root;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        CodeGalaxy codeGalaxy = new CodeGalaxy();
        root.setCenter(codeGalaxy);
        root.setPrefSize(500,300);
    }
}