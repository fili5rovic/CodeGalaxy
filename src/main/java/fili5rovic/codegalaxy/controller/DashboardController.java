package fili5rovic.codegalaxy.controller;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.dashboardHelper.ButtonManager;
import fili5rovic.codegalaxy.dashboardHelper.ConsoleHelper;
import fili5rovic.codegalaxy.dashboardHelper.MenuManager;
import fili5rovic.codegalaxy.lsp.LSP;
import fili5rovic.codegalaxy.settings.ProjectSettings;
import fili5rovic.codegalaxy.dashboardHelper.ProjectManager;
import fili5rovic.codegalaxy.util.SVGUtil;
import fili5rovic.codegalaxy.window.Window;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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

    @FXML
    private MenuItem settings;

    @FXML
    private Button showHierarchyBtn;

    @FXML
    private StackPane bottomStackPane;

    @FXML
    private BorderPane consolePane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Window.getWindowAt(Window.WINDOW_DASHBOARD).setController(this);

        try {
            LSP.instance().start();
        } catch (Exception e) {
            System.out.println("Failed to start LSP server: " + e.getMessage());
        }

        tryToOpenLastProject();
        MenuManager.initialize();
        ButtonManager.initialize();
        ConsoleHelper.initialize();
    }



    private void tryToOpenLastProject() {
        String lastProjectPath = ProjectSettings.getInstance().get("lastProjectPath");
        if (lastProjectPath == null)
            return;
        File lastProjectFile = new File(lastProjectPath);
        Path lastProjectPathFile = lastProjectFile.toPath();
        if (lastProjectFile.exists() && lastProjectFile.isDirectory()) {
            ProjectManager.openProject(lastProjectPathFile);
        } else {
            System.out.println("Last project path is not valid.");
        }

        List<String> recentFiles = ProjectSettings.getInstance().getMultiple("recentFiles");
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
            LSP.instance().openFile(codeGalaxy.getFilePath().toString());
        } catch (Exception e) {
            System.out.println("Failed to open file: " + e.getMessage());
        }

        ProjectSettings.getInstance().addTo("recentFiles", filePath.toString());
        ImageView icon = SVGUtil.getIconByPath(filePath, 16, 16, -2);

        Tab tab = new Tab(fileName, codeGalaxy);
        tab.setGraphic(icon);
        tab.setOnClosed(_ -> closedTab(filePath));

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().selectLast();
    }

    private void closedTab(Path filePath) {
        LSP.instance().closeFile(filePath.toString());
        ProjectSettings.getInstance().removeFrom("recentFiles", filePath.toString());
    }

    public void onAppClose(WindowEvent event) {
        System.out.println("App closed");
        LSP.instance().stop();
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

    public MenuItem getOpen() {
        return open;
    }

    public MenuItem getSaveAll() {
        return saveAll;
    }

    public MenuItem getNewProject() {
        return newProject;
    }

    public MenuItem getUndo() {
        return undo;
    }

    public MenuItem getRedo() {
        return redo;
    }

    public MenuItem getCut() {
        return cut;
    }

    public MenuItem getCopy() {
        return copy;
    }

    public MenuItem getPaste() {
        return paste;
    }

    public MenuItem getDelete() {
        return delete;
    }

    public MenuItem getSelectAll() {
        return selectAll;
    }

    public MenuItem getSettings() {
        return settings;
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public Button getShowHierarchyBtn() {
        return showHierarchyBtn;
    }

    public StackPane getBottomStackPane() {
        return bottomStackPane;
    }

    public BorderPane getConsolePane() {
        return consolePane;
    }


    //</editor-fold>
}