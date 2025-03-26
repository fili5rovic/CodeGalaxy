package fili5rovic.codegalaxy.controller;

import fili5rovic.codegalaxy.Main;
import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.hierarchy.ProjectHierarchy;
import fili5rovic.codegalaxy.util.FileHelper;
import fili5rovic.codegalaxy.window.Window;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class DashboardController extends ControllerBase {
    @FXML
    private Pane filePane;

    @FXML
    private TabPane tabPane;

    @FXML
    private BorderPane treeViewPane;

    @FXML
    private TextField fileNameTextField;

    @FXML
    private Label fileNameLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Window.getWindowAt(Window.WINDOW_DASHBOARD).setController(this);

        String path = Main.class.getResource("/fili5rovic/codegalaxy/sampleCode/code.txt").getPath();
        createTab(Path.of(path.substring(1)));

        // init for now
        treeViewPane.setCenter(new ProjectHierarchy("C:\\Users\\fili5\\OneDrive\\Desktop\\test"));
    }

    public void createTab(Path filePath) {
        String fileName = filePath.getFileName().toString();
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals(fileName)) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }

        CodeGalaxy codeGalaxy = new CodeGalaxy();
        codeGalaxy.insertText(0, FileHelper.readFromFile(filePath.toString()));
        tabPane.getTabs().add(new Tab(fileName, codeGalaxy));
        tabPane.getSelectionModel().selectLast();
    }

    public void onAppClose(WindowEvent actionEvent) {
        System.out.println("App closed");
    }

    //<editor-fold desc="Getters">
    public TextField getFileNameTextField() {
        return fileNameTextField;
    }

    public Label getFileNameLabel() {
        return fileNameLabel;
    }

    public Pane getFilePane() {
        return filePane;
    }
    //</editor-fold>
}