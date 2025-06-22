package fili5rovic.codegalaxy.controller;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.dashboardHelper.*;
import fili5rovic.codegalaxy.errors.DisplayErrorsHandler;
import fili5rovic.codegalaxy.lsp.LSP;
import fili5rovic.codegalaxy.notification.NotificationManager;
import fili5rovic.codegalaxy.vcs.treeView.GitHierarchy;
import fili5rovic.codegalaxy.window.Window;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class DashboardController extends ControllerBase {

    @FXML
    private TabPane tabPane;

    @FXML
    private BorderPane treeViewPane;

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
    private TabPane consoleTabPane;

    @FXML
    private TabPane errorTabPane;

    @FXML
    private VBox errorVBox;

    @FXML
    private SplitPane mainSplitPane;

    @FXML
    private BorderPane infoPaneNoTabs;

    @FXML
    private SplitPane consoleSplitPane;

    @FXML
    private ToggleButton showHierarchyToggle;

    @FXML
    private ToggleButton showGitToggle;

    @FXML
    private ToggleButton showProblemsToggle;

    @FXML
    private ToggleButton showRunToggle;

    @FXML
    private BorderPane gitInitPane;

    @FXML
    private BorderPane gitBorderPane;

    @FXML
    private StackPane gitPane;

    @FXML
    private Button initRepoBtn;

    @FXML
    private Button commitBtn;

    @FXML
    private BorderPane gitTreeViewPane;

    @FXML
    private TextArea commitMsg;

    @FXML
    private VBox notificationVBox;

    private DisplayErrorsHandler displayErrorsHandler;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Window.getWindowAt(Window.WINDOW_DASHBOARD).setController(this);

        ProjectManager.checkForValidWorkspace().thenAcceptAsync(success -> {
            if (!success) {
                Platform.exit();
                Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage().close();
                return;
            }

            try {
                LSP.instance().start();
                Platform.runLater(ProjectManager::tryToOpenLastProject);
            } catch (Exception e) {
                System.err.println("Failed to start LSP server: " + e.getMessage());
                System.err.println("Fatal error: LSP server is not running. Please check your configuration.");
            }
        });

        MenuManager.initialize();
        ToggleManager.initialize();
        ButtonManager.initialize();
        SplitPaneManager.setupLockPositions();
        TooltipManager.init();

        tabPane.getTabs().addListener((ListChangeListener<Tab>) _ -> updateInfoPaneVisibility());

        updateInfoPaneVisibility();

        this.displayErrorsHandler = new DisplayErrorsHandler();
        this.displayErrorsHandler.init();

        fileSearchPopupListener();

        GitHierarchy.addHierarchy();

        Platform.runLater(() -> {
            NotificationManager.show("Test","Test 123");
        });
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

    public TabPane getConsoleTabPane() {
        return consoleTabPane;
    }

    public TabPane getErrorTabPane() {
        return errorTabPane;
    }

    public SplitPane getMainSplitPane() {
        return mainSplitPane;
    }

    public SplitPane getConsoleSplitPane() {
        return consoleSplitPane;
    }

    public VBox getErrorVBox() {
        return errorVBox;
    }

    public ToggleButton getShowProblemsToggle() {
        return showProblemsToggle;
    }

    public ToggleButton getShowRunToggle() {
        return showRunToggle;
    }

    public ToggleButton getShowHierarchyToggle() {
        return showHierarchyToggle;
    }

    public ToggleGroup getConsoleToggleGroup() {
        return showRunToggle.getToggleGroup();
    }

    public ToggleGroup getLeftToggleGroup() {
        return showGitToggle.getToggleGroup();
    }

    public DisplayErrorsHandler getDisplayErrorsHandler() {
        return displayErrorsHandler;
    }

    public BorderPane getGitBorderPane() {
        return gitBorderPane;
    }

    public ToggleButton getShowGitToggle() {
        return showGitToggle;
    }

    public StackPane getGitPane() {
        return gitPane;
    }

    public BorderPane getGitInitPane() {
        return gitInitPane;
    }

    public Button getInitRepoBtn() {
        return initRepoBtn;
    }

    public Button getCommitBtn() {
        return commitBtn;
    }

    public TextArea getCommitMsg() {
        return commitMsg;
    }

    public VBox getNotificationVBox() {
        return notificationVBox;
    }

    public BorderPane getGitTreeViewPane() {
        return gitTreeViewPane;
    }

    //</editor-fold>
}