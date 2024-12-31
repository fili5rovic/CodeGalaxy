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
        codeGalaxy.insertText(0, "package fili5rovic.codegalaxy;\n" +
                "\n" +
                "import fili5rovic.codegalaxy.code.CodeGalaxy;\n" +
                "import javafx.fxml.FXML;\n" +
                "import javafx.fxml.Initializable;\n" +
                "import javafx.scene.layout.BorderPane;\n" +
                "\n" +
                "import java.net.URL;\n" +
                "import java.util.ResourceBundle;\n" +
                "\n" +
                "public class FXMLController implements Initializable {\n" +
                "    @FXML\n" +
                "    private BorderPane root;\n" +
                "\n" +
                "    @Override\n" +
                "    public void initialize(URL url, ResourceBundle resourceBundle) {\n" +
                "        CodeGalaxy codeGalaxy = new CodeGalaxy();\n" +
                "        codeGalaxy.insertText(0, \"Test\");\n" +
                "        root.setCenter(codeGalaxy);\n" +
                "        root.setPrefSize(500,300);\n" +
                "    }\n" +
                "}");
        root.setCenter(codeGalaxy);
        root.setPrefSize(1000,500);
    }
}