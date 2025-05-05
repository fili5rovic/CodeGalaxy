package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.util.FileHelper;
import fili5rovic.codegalaxy.util.SVGUtil;
import fili5rovic.codegalaxy.window.Window;
import fili5rovic.codegalaxy.window.WindowHelper;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;

import java.io.File;

public class MenuManager {

    private static final DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);

    private static final TabPane tabPane = controller.getTabPane();

    public static void initialize() {
        menuIcons();
        menuActions();
    }

    private static void menuActions() {
        controller.getOpen().setOnAction(MenuManager::chooseFolder);
        controller.getSaveAll().setOnAction(MenuManager::saveAllFiles);
        controller.getNewProject().setOnAction(MenuManager::newProject);
        controller.getUndo().setOnAction(MenuManager::undo);
        controller.getRedo().setOnAction(MenuManager::redo);
        controller.getCut().setOnAction(MenuManager::cut);
        controller.getCopy().setOnAction(MenuManager::copy);
        controller.getPaste().setOnAction(MenuManager::paste);
        controller.getSelectAll().setOnAction(MenuManager::selectAll);
        controller.getDelete().setOnAction(MenuManager::delete);
        controller.getSettings().setOnAction(MenuManager::settings);
    }

    private static void menuIcons() {
        controller.getOpen().setGraphic(SVGUtil.getUI("openProject", 16, 16));
        controller.getSaveAll().setGraphic(SVGUtil.getUI("saveAll", 16, 16));
        controller.getNewProject().setGraphic(SVGUtil.getUI("newProject", 16, 16));
        controller.getUndo().setGraphic(SVGUtil.getUI("undo", 16, 16));
        controller.getRedo().setGraphic(SVGUtil.getUI("redo", 16, 16));
        controller.getCut().setGraphic(SVGUtil.getUI("cut", 16, 16));
        controller.getCopy().setGraphic(SVGUtil.getUI("copy", 16, 16));
        controller.getPaste().setGraphic(SVGUtil.getUI("paste", 16, 16));
        controller.getDelete().setGraphic(SVGUtil.getUI("delete", 16, 16));
        controller.getSelectAll().setGraphic(SVGUtil.getUI("selectAll", 16, 16));
        controller.getSettings().setGraphic(SVGUtil.getUI("settings", 16, 16));
    }

    //<editor-fold desc="MenuActions">
    private static void chooseFolder(ActionEvent a) {
        File folder = FileHelper.openFolderChooser(Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage());
        if (folder != null && folder.isDirectory()) {
            ProjectManager.openProject(folder.getAbsoluteFile().toPath());
        }
    }

    private static void saveAllFiles(ActionEvent e) {
        for (Tab tab : tabPane.getTabs()) {
            CodeGalaxy codeGalaxy = ((CodeGalaxy) tab.getContent());
            codeGalaxy.save();
        }
    }

    private static void newProject(ActionEvent a) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Project");
        dialog.setHeaderText("Enter project name:");
        dialog.setContentText("Project name:");

        dialog.showAndWait().ifPresent(ProjectManager::createProject);
    }


    private static void undo(ActionEvent e) {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            CodeGalaxy codeGalaxy = (CodeGalaxy) selectedTab.getContent();
            codeGalaxy.undo();
        }
    }

    private static void redo(ActionEvent e) {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            CodeGalaxy codeGalaxy = (CodeGalaxy) selectedTab.getContent();
            codeGalaxy.redo();
        }
    }

    private static void cut(ActionEvent e) {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            CodeGalaxy codeGalaxy = (CodeGalaxy) selectedTab.getContent();
            codeGalaxy.selectLine();
            codeGalaxy.cut();
        }
    }

    private static void copy(ActionEvent e) {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            CodeGalaxy codeGalaxy = (CodeGalaxy) selectedTab.getContent();
            codeGalaxy.selectLine();
            codeGalaxy.copy();
        }
    }

    private static void paste(ActionEvent e) {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            CodeGalaxy codeGalaxy = (CodeGalaxy) selectedTab.getContent();
            codeGalaxy.paste();
        }
    }

    private static void selectAll(ActionEvent e) {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            CodeGalaxy codeGalaxy = (CodeGalaxy) selectedTab.getContent();
            codeGalaxy.selectAll();
        }
    }

    private static void delete(ActionEvent e) {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            CodeGalaxy codeGalaxy = (CodeGalaxy) selectedTab.getContent();
            codeGalaxy.deleteNextChar();
        }
    }

    private static void settings(ActionEvent e) {
        WindowHelper.showWindow(Window.SETTINGS);
    }
    //</editor-fold>
}
