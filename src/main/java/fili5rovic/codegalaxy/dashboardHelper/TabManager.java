package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.controller.Controllers;
import fili5rovic.codegalaxy.lsp.LSP;
import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.util.SVGUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import java.nio.file.Path;

public class TabManager {


    public static void createTab(Path filePath) {
        TabPane tabPane = Controllers.dashboardController().getTabPane();
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getId().equals(filePath.toString())) {
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

        IDESettings.getInstance().addTo("recentFiles", filePath.toString());

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
    }

    private static void makeTabGraphic(Path filePath, Tab tab) {
        ImageView icon = SVGUtil.getIconByPath(filePath, 12, 12, -2);

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
        hbox.setSpacing(5);
        hbox.setAlignment(Pos.CENTER_LEFT);
        tab.setGraphic(hbox);
    }

    private static void closedTab(Path filePath) {
        LSP.instance().closeFile(filePath.toString());
        IDESettings.getInstance().removeFrom("recentFiles", filePath.toString());
    }

    private static void onMouseEnter(MouseEvent e) {
        if (e.getSource() instanceof HBox hbox) {
            if (hbox.getChildren().getLast() instanceof Button button) {
                button.setVisible(true);
            }
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
