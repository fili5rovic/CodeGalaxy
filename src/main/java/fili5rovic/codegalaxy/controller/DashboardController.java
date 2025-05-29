package fili5rovic.codegalaxy.controller;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.dashboardHelper.*;
import fili5rovic.codegalaxy.errors.DisplayErrorsHandler;
import fili5rovic.codegalaxy.lsp.LSP;
import fili5rovic.codegalaxy.window.Window;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

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
    private Button showRunBtn;

    @FXML
    private Button showErrorsBtn;

    @FXML
    private TabPane consoleTabPane;

    @FXML
    private TabPane errorTabPane;

    @FXML
    private VBox errorVBox;

    @FXML
    private SplitPane mainSplitPane;

    @FXML
    private Pane infoPaneNoTabs;

    private DisplayErrorsHandler displayErrorsHandler;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Window.getWindowAt(Window.WINDOW_DASHBOARD).setController(this);

        ProjectManager.checkForValidWorkspace();

        CompletableFuture.runAsync(() -> {
            try {
                LSP.instance().start();
            } catch (Exception e) {
                System.out.println("Failed to start LSP server: " + e.getMessage());
            }
        }).thenRunAsync(ProjectManager::tryToOpenLastProject, Platform::runLater);

        MenuManager.initialize();
        ButtonManager.initialize();
        SplitPaneManager.setupLockPositions();
        TooltipManager.init();

//        ProjectManager.checkForValidWorkspace();

        tabPane.getTabs().addListener((ListChangeListener<Tab>) _ -> updateInfoPaneVisibility());

        updateInfoPaneVisibility();

        this.displayErrorsHandler = new DisplayErrorsHandler();
        this.displayErrorsHandler.init();

        fileSearchPopupListener();

    }

    private static void fileSearchPopupListener() {
        Platform.runLater(() -> {
            Scene scene = Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage().getScene();
            javafx.stage.Window window = scene.getWindow();

            scene.setOnKeyPressed(event -> {
                if (event.isControlDown() && event.getCode().toString().equals("E")) {
                    Popup popup = FileFinder.getInstance().popup();

                    if (!popup.isShowing()) {
                        popup.show(window);
                        popup.hide();
                    }

                    double popupWidth = popup.getWidth();
                    double popupHeight = popup.getHeight();

                    double centerX = window.getX() + (window.getWidth() - popupWidth) / 2;
                    double centerY = window.getY() + (window.getHeight() - popupHeight) / 2;

                    popup.show(window, centerX, centerY);
                }
            });
        });
    }

    private void updateInfoPaneVisibility() {
        boolean hasTabs = !tabPane.getTabs().isEmpty();
        infoPaneNoTabs.setVisible(!hasTabs);
    }

    public void createTab(Path filePath) {
        TabManager.createTab(filePath);
    }

    public CodeGalaxy getOpenCodeGalaxy() {
        if (tabPane.getSelectionModel().getSelectedItem() != null) {
            return (CodeGalaxy) tabPane.getSelectionModel().getSelectedItem().getContent();
        }
        return null;
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

    public TabPane getConsoleTabPane() {
        return consoleTabPane;
    }

    public TabPane getErrorTabPane() {
        return errorTabPane;
    }

    public SplitPane getMainSplitPane() {
        return mainSplitPane;
    }

    public VBox getErrorVBox() {
        return errorVBox;
    }

    public Button getShowRunBtn() {
        return showRunBtn;
    }

    public Button getShowErrorsBtn() {
        return showErrorsBtn;
    }

    public DisplayErrorsHandler getDisplayErrorsHandler() {
        return displayErrorsHandler;
    }


    //</editor-fold>
}