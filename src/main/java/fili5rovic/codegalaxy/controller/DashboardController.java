package fili5rovic.codegalaxy.controller;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.lsp.LSPManager;
import fili5rovic.codegalaxy.preferences.UserPreferences;
import fili5rovic.codegalaxy.project.ProjectManager;
import fili5rovic.codegalaxy.util.FileHelper;
import fili5rovic.codegalaxy.util.SVGUtil;
import fili5rovic.codegalaxy.window.Window;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
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

    @FXML
    private MenuItem undo;

    @FXML
    private MenuItem redo;

    @FXML
    private MenuItem cut;

    @FXML
    private MenuItem copy;

    @FXML
    private MenuItem paste;

    @FXML
    private MenuItem delete;

    @FXML
    private MenuItem selectAll;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Window.getWindowAt(Window.WINDOW_DASHBOARD).setController(this);

        try {
            LSPManager.getInstance().start();
        } catch (Exception e) {
            System.out.println("Failed to start LSP server: " + e.getMessage());
        }

        tryToOpenLastProject();
        menuItemListeners();

        menuIcons();
    }

    private void menuIcons() {
        open.setGraphic(SVGUtil.getUI("openProject", 16, 16));
        saveAll.setGraphic(SVGUtil.getUI("saveAll", 16, 16));
        newProject.setGraphic(SVGUtil.getUI("newProject", 16, 16));
        undo.setGraphic(SVGUtil.getUI("undo", 16, 16));
        redo.setGraphic(SVGUtil.getUI("redo", 16, 16));
        cut.setGraphic(SVGUtil.getUI("cut", 16, 16));
        copy.setGraphic(SVGUtil.getUI("copy", 16, 16));
        paste.setGraphic(SVGUtil.getUI("paste", 16, 16));
        delete.setGraphic(SVGUtil.getUI("delete", 16, 16));
        selectAll.setGraphic(SVGUtil.getUI("selectAll", 16, 16));
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

        List<String> recentFiles = UserPreferences.getInstance().getMultiple("recentFiles");
        for (String filePath : recentFiles) {
            Path path = Path.of(filePath);
            if (path.toFile().exists()) {
                createTab(path);
            }
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
        try {
            LSPManager.getInstance().openFile(codeGalaxy.getFilePath().toString());
        } catch (Exception e) {
            System.out.println("Failed to open file: " + e.getMessage());
        }

        UserPreferences.getInstance().addTo("recentFiles", filePath.toString());
        ImageView icon = SVGUtil.getIconByPath(filePath, 16, 16, -2);

        Tab tab = new Tab(fileName, codeGalaxy);
        tab.setGraphic(icon);
        tab.setOnClosed(_ -> closedTab(filePath));

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().selectLast();
    }

    private void closedTab(Path filePath) {
        LSPManager.getInstance().closeFile(filePath.toString());
        UserPreferences.getInstance().removeFrom("recentFiles", filePath.toString());
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
        selectAll.setOnAction(_ -> {
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null) {
                CodeGalaxy codeGalaxy = (CodeGalaxy) selectedTab.getContent();
                codeGalaxy.selectAll();
            }
        });
        undo.setOnAction(_ -> {
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null) {
                CodeGalaxy codeGalaxy = (CodeGalaxy) selectedTab.getContent();
                codeGalaxy.undo();
            }
        });
        redo.setOnAction(_ -> {
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null) {
                CodeGalaxy codeGalaxy = (CodeGalaxy) selectedTab.getContent();
                codeGalaxy.redo();
            }
        });
        cut.setOnAction(_ -> {
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null) {
                CodeGalaxy codeGalaxy = (CodeGalaxy) selectedTab.getContent();
                codeGalaxy.selectLine();
                codeGalaxy.cut();
            }
        });
        copy.setOnAction(_ -> {
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null) {
                CodeGalaxy codeGalaxy = (CodeGalaxy) selectedTab.getContent();
                codeGalaxy.selectLine();
                codeGalaxy.copy();
            }
        });
        paste.setOnAction(_ -> {
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null) {
                CodeGalaxy codeGalaxy = (CodeGalaxy) selectedTab.getContent();
                codeGalaxy.paste();
            }
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
            CodeGalaxy codeGalaxy = ((CodeGalaxy) tab.getContent());
            codeGalaxy.save();
        }

    }

    public void onAppClose(WindowEvent actionEvent) {
        System.out.println("App closed");
        LSPManager.getInstance().stop();
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