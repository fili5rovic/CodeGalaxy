package fili5rovic.codegalaxy.controller;

import fili5rovic.codegalaxy.settings.ProjectSettings;
import fili5rovic.codegalaxy.util.SVGUtil;
import fili5rovic.codegalaxy.util.SettingsMenuHelper;
import fili5rovic.codegalaxy.window.Window;
import fili5rovic.codegalaxy.window.WindowHelper;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController extends ControllerBase {

    @FXML
    private TreeView<String> settingsTreeView;

    @FXML
    private SplitPane splitPane;

    @FXML
    private BorderPane settingsPane;

    @FXML
    private Button apply;

    @FXML
    private Button cancel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        Window.getWindowAt(Window.SETTINGS).setController(this);
        Window.getWindowAt(0).setController(this);

        initTreeView();

        buttonActions();
    }

    private void buttonActions() {
        apply.setOnAction(_ -> {
            ProjectSettings.applyTempSettings();
        });

        cancel.setOnAction(_ -> {
            WindowHelper.hideWindow(Window.SETTINGS);
        });

        apply.setOnMouseEntered(_ -> apply.setGraphic(SVGUtil.getEmoji("perfect",16,16)));
        apply.setOnMouseExited(_ -> apply.setGraphic(null));

        cancel.setOnMouseEntered(_ -> cancel.setGraphic(SVGUtil.getEmoji("nope", 16, 16)));
        cancel.setOnMouseExited(_ -> cancel.setGraphic(null));
    }


    private void initTreeView() {
        TreeItem<String> rootItem = new TreeItem<>(null);
        rootItem.setExpanded(true);

        TreeItem<String> general = new TreeItem<>("General");
        TreeItem<String> appearance = new TreeItem<>("Appearance");

        appearance.getChildren().add(new TreeItem<>("Theme"));


        rootItem.getChildren().addAll(general, appearance);

        settingsTreeView.setRoot(rootItem);
        settingsTreeView.setShowRoot(false);

        settingsTreeView.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setGraphic(null);
                }
            }
        });

        settingsTreeView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && newSelection.isLeaf()) {
                Node item = SettingsMenuHelper.getSettingsMenuItem(newSelection.getValue());
                settingsPane.setCenter(item);
            }
        });
    }



}
