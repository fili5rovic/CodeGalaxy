package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.controller.Controllers;
import fili5rovic.codegalaxy.lsp.LSP;
import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.util.SVGUtil;
import fili5rovic.codegalaxy.util.AnimUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

import java.nio.file.Path;

public class TabManager {

    public static Tab createTab(Path filePath) {
        TabPane tabPane = Controllers.dashboardController().getTabPane();
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getId().equals(filePath.toString())) {
                tabPane.getSelectionModel().select(tab);
                return tab;
            }
        }
        CodeGalaxy codeGalaxy = new CodeGalaxy(filePath);
        try {
            LSP.instance().openFile(codeGalaxy.getFilePath().toString());
        } catch (Exception e) {
            System.out.println("Failed to open file: " + e.getMessage());
        }

        Path projectPath = Path.of(IDESettings.getRecentInstance().get("lastProjectPath"));
        Path relativePath = projectPath.relativize(filePath);

        IDESettings.getRecentInstance().addTo("recentFiles", relativePath.toString());

        Tab tab = new Tab();
        tab.setId(filePath.toString());
        makeTabGraphic(filePath, tab);
        tab.setContent(codeGalaxy);
        tab.setClosable(false);

        tab.setOnSelectionChanged(_ -> {
            if (tab.isSelected()) {
                codeGalaxy.requestFocus();
                Controllers.dashboardController().getDisplayErrorsHandler().displayErrors();
            }
        });
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().selectLast();
        AnimUtil.animateTabBorder(tab);
        return tab;
    }


    private static void makeTabGraphic(Path filePath, Tab tab) {
        ImageView icon = SVGUtil.getIconByPath(filePath, 16, 0);

        Label label = new Label(filePath.getFileName().toString());
        label.getStyleClass().clear();
        label.getStyleClass().add("tab-label");
        Button button = new Button("✕");
        button.setFont(new Font(10));
        button.getStyleClass().clear();
        button.getStyleClass().add("tab-close-button");
        button.setOnAction(_ -> {
            TabPane tabPane = tab.getTabPane();
            if (tabPane != null) {
                tabPane.getTabs().remove(tab);
                closedTab(filePath);
            }
        });
        button.setVisible(false);

        HBox hbox = new HBox(icon, label, button);
        hbox.setOnMouseEntered(TabManager::onMouseEnter);
        hbox.setOnMouseExited(TabManager::onMouseExit);
        hbox.setOnMouseClicked(e -> onMouseClicked(e, tab));
        hbox.setSpacing(5);
        hbox.setAlignment(Pos.CENTER_LEFT);
        tab.setGraphic(hbox);


    }

    public static void createTab(Pane pane, String title) {
        TabPane tabPane = Controllers.dashboardController().getTabPane();
        Tab tab = new Tab();
        tab.setContent(pane);
        tab.setClosable(false);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().selectLast();
        makeTabGraphic(tab, SVGUtil.getIcon("file", 16), title);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().selectLast();

        AnimUtil.animateTabBorder(tab);
    }

    private static void makeTabGraphic(Tab tab, ImageView icon, String title) {
        Label label = new Label(title);
        label.getStyleClass().clear();
        label.getStyleClass().add("tab-label");
        Button button = new Button("✕");
        button.setFont(new Font(10));
        button.getStyleClass().clear();
        button.getStyleClass().add("tab-close-button");
        button.setOnAction(_ -> {
            TabPane tabPane = tab.getTabPane();
            if (tabPane != null) {
                tabPane.getTabs().remove(tab);
            }
        });
        button.setVisible(false);

        HBox hbox = new HBox(icon, label, button);
        hbox.setOnMouseEntered(TabManager::onMouseEnter);
        hbox.setOnMouseExited(TabManager::onMouseExit);
        hbox.setOnMouseClicked(e -> onMouseClicked(e, tab));
        hbox.setSpacing(5);
        hbox.setAlignment(Pos.CENTER_LEFT);
        tab.setGraphic(hbox);
    }

    private static void closedTab(Path filePath) {
        LSP.instance().closeFile(filePath.toString());

        Path projectPath = Path.of(IDESettings.getRecentInstance().get("lastProjectPath"));
        Path relativePath = projectPath.relativize(filePath);

        IDESettings.getRecentInstance().removeFrom("recentFiles", relativePath.toString());
    }

    private static void onMouseEnter(MouseEvent e) {
        if (e.getSource() instanceof HBox hbox) {
            if (hbox.getChildren().getLast() instanceof Button button) {
                button.setVisible(true);
            }
        }
    }

    private static void onMouseClicked(MouseEvent e, Tab tab) {
        if (e.getButton() == MouseButton.MIDDLE) {
            tab.getTabPane().getTabs().remove(tab);
            closedTab(((CodeGalaxy) tab.getContent()).getFilePath());
        }
    }

    private static void onMouseExit(MouseEvent e) {
        if (e.getSource() instanceof HBox hbox) {
            if (hbox.getChildren().getLast() instanceof Button button) {
                button.setVisible(false);
            }
        }
    }
}
