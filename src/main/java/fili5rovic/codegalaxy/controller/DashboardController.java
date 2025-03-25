package fili5rovic.codegalaxy.controller;

import fili5rovic.codegalaxy.Main;
import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.hierarchy.ProjectHierarchy;
import fili5rovic.codegalaxy.util.FileHelper;
import fili5rovic.codegalaxy.window.Window;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController extends ControllerBase {
    @FXML
    private Pane filePane;

    @FXML
    private BorderPane root;

    @FXML
    private BorderPane treeViewPane;

    @FXML
    private TextField fileNameTextField;

    @FXML
    private Label fileNameLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Window.getWindowAt(Window.WINDOW_DASHBOARD).setController(this);
        CodeGalaxy codeGalaxy = new CodeGalaxy();
        String path = Main.class.getResource("/fili5rovic/codegalaxy/sampleCode/code.txt").getPath();
        codeGalaxy.insertText(0, FileHelper.readFromFile(path));

        root.setCenter(codeGalaxy);
        root.setPrefSize(1000,500);

        path = "C:\\Users\\fili5\\OneDrive\\Desktop\\test";
        treeViewPane.setCenter(new ProjectHierarchy(path));

        listeners();
    }

    public void listeners() {
        fileNameTextField.setOnAction(e -> {
            System.out.println("Creating new file: " + fileNameTextField.getText());
            filePane.setVisible(false);
        });
    }

    public void onAppClose(WindowEvent actionEvent) {
        System.out.println("App closed");
    }

    public TextField getFileNameTextField() {
        return fileNameTextField;
    }

    public Label getFileNameLabel() {
        return fileNameLabel;
    }

    public Pane getFilePane() {
        return filePane;
    }
}