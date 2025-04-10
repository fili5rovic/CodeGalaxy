package fili5rovic.codegalaxy.controller;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.preferences.UserPreferences;
import fili5rovic.codegalaxy.project.ProjectManager;
import fili5rovic.codegalaxy.util.FileHelper;
import fili5rovic.codegalaxy.window.Window;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import java.io.File;
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

    @FXML
    private MenuItem open;

    @FXML
    private MenuItem saveAll;

    @FXML
    private MenuItem newProject;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Window.getWindowAt(Window.WINDOW_DASHBOARD).setController(this);
        tryToOpenLastProject();
        menuItemListeners();
    }

    private void tryToOpenLastProject() {
        String lastProjectPath = UserPreferences.getInstance().get("lastProjectPath");
        if(lastProjectPath == null)
            return;
        File lastProjectFile = new File(lastProjectPath);
        Path lastProjectPathFile = lastProjectFile.toPath();
        if (lastProjectFile.exists() && lastProjectFile.isDirectory()) {
            ProjectManager.openProject(lastProjectPathFile);
        } else {
            System.out.println("Last project path is not valid.");
        }
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
        codeGalaxy.setFile(filePath);

        tabPane.getTabs().add(new Tab(fileName, codeGalaxy));
        tabPane.getSelectionModel().selectLast();
    }

    public void menuItemListeners() {
        open.setOnAction(_ -> chooseFolder());
        saveAll.setOnAction(_ -> saveAll());
        newProject.setOnAction(_ -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("New Project");
            dialog.setHeaderText("Enter project name:");
            dialog.setContentText("Project name:");

            dialog.showAndWait().ifPresent(ProjectManager::createProject);
        });
    }

    private void chooseFolder() {
        File folder = FileHelper.openFolderChooser(Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage());
        if (folder != null && folder.isDirectory()) {
            ProjectManager.openProject(folder.getAbsoluteFile().toPath());
        }
    }

    private void saveAll() {
        for (Tab tab : tabPane.getTabs()) {
            CodeGalaxy codeGalaxy = (CodeGalaxy) tab.getContent();
            codeGalaxy.save();
        }
    }

    public void onAppClose(WindowEvent actionEvent) {
        System.out.println("App closed");
    }

    //<editor-fold desc="Getters">

    public BorderPane getTreeViewPane() {
        return treeViewPane;
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
    //</editor-fold>
}