package fili5rovic.codegalaxy.controller;

import fili5rovic.codegalaxy.Main;
import fili5rovic.codegalaxy.code.manager.editing.shortcuts.keystate.KeyState;
import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.settings.ShortcutsTableHelper;
import fili5rovic.codegalaxy.util.SVGUtil;
import fili5rovic.codegalaxy.window.Window;
import fili5rovic.codegalaxy.window.WindowHelper;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class SettingsController extends ControllerBase {

    @FXML
    private TreeView<String> settingsTreeView;


    @FXML
    private BorderPane settingsPane;

    @FXML
    private Button apply;

    @FXML
    private Button cancel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Window.getWindowAt(Window.SETTINGS).setController(this);
        initTreeView();
        buttonActions();

    }


    private void buttonActions() {
        apply.setOnAction(_ -> {
            IDESettings.applyTempSettings();
            WindowHelper.hideWindow(Window.SETTINGS);
        });

        cancel.setOnAction(_ -> WindowHelper.hideWindow(Window.SETTINGS));

        apply.setOnMouseEntered(_ -> apply.setGraphic(SVGUtil.getEmoji("perfect", 16, 16)));
        apply.setOnMouseExited(_ -> apply.setGraphic(null));

        cancel.setOnMouseEntered(_ -> cancel.setGraphic(SVGUtil.getEmoji("nope", 16, 16)));
        cancel.setOnMouseExited(_ -> cancel.setGraphic(null));
    }


    private void initTreeView() {
        TreeItem<String> rootItem = new TreeItem<>(null);
        rootItem.setExpanded(true);

        TreeItem<String> general = new TreeItem<>("General");
        general.getChildren().add(new TreeItem<>("Shortcuts"));

        TreeItem<String> appearance = new TreeItem<>("Appearance");
        appearance.getChildren().add(new TreeItem<>("Theme"));


        rootItem.getChildren().add(general);
        rootItem.getChildren().add(appearance);

        settingsTreeView.setRoot(rootItem);
        settingsTreeView.setShowRoot(false);

        settingsTreeView.setCellFactory(_ -> new TreeCell<>() {
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

        settingsTreeView.getSelectionModel().selectedItemProperty().addListener((_, _, newSelection) -> {
            if (newSelection != null && newSelection.isLeaf()) {
                Node item = getSettingsMenuItem(newSelection.getValue());
                settingsPane.setCenter(item);
            }
        });
    }

    private Node getSettingsMenuItem(String name) {
        return switch (name) {
            case "Theme" -> getThemeSettingsMenuItem();
            case "Shortcuts" -> getShortcutsSettingsMenuItem();
            default -> new Label(name);
        };
    }

    private Node getShortcutsSettingsMenuItem() {
        VBox shortcutsSettingsMenu = new VBox();
        shortcutsSettingsMenu.setAlignment(Pos.TOP_CENTER);
        shortcutsSettingsMenu.setSpacing(10);

        shortcutsSettingsMenu.getChildren().add(new Label("Code actions"));

        TableView<KeyState> shortcutsTable = ShortcutsTableHelper.getShortcutsTable();

        shortcutsSettingsMenu.getChildren().add(shortcutsTable);


        return shortcutsSettingsMenu;
    }

    private static Node getThemeSettingsMenuItem() {
        VBox themeSettingsMenu = new VBox();
        themeSettingsMenu.setAlignment(Pos.TOP_CENTER);
        themeSettingsMenu.setSpacing(10);

        themeSettingsMenu.getChildren().add(new Label("Editor theme"));

        ComboBox<Label> themeComboBox = new ComboBox<>();
        Label light = createThemeLabel("Light");
        Label dark = createThemeLabel("Dark");

        themeComboBox.getItems().addAll(dark, light);
        themeComboBox.setValue(dark);

        themeComboBox.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(createThemeLabel(item.getText()));
                }
            }
        });

        themeComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(createThemeLabel(item.getText()));
                }
            }
        });

        themeComboBox.setOnAction(_ -> {
            Label selectedTheme = themeComboBox.getValue();
            if (selectedTheme != null) {
                selectTheme(selectedTheme.getText());
            }
        });

        themeSettingsMenu.getChildren().add(themeComboBox);

        return themeSettingsMenu;
    }

    private static Label createThemeLabel(String themeName) {
        Label label = new Label(themeName);
        label.setContentDisplay(ContentDisplay.LEFT);
        label.setGraphicTextGap(5);

        switch (themeName) {
            case "Light" -> label.setGraphic(SVGUtil.getUI("sun", 16, 16));
            case "Dark" -> label.setGraphic(SVGUtil.getUI("moon", 16, 16));
        }

        return label;
    }

    private static void selectTheme(String theme) {
        final Scene dashboardScene = Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage().getScene();
        final Scene settingsScene = Window.getWindowAt(Window.SETTINGS).getStage().getScene();
        switch (theme) {
            case "Light" -> {
                dashboardScene.getStylesheets().remove(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/main-dark.css")).toExternalForm());
                dashboardScene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/main-light.css")).toExternalForm());
                dashboardScene.getStylesheets().remove(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/codegalaxy-dark.css")).toExternalForm());
                dashboardScene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/codegalaxy-light.css")).toExternalForm());

                settingsScene.getStylesheets().remove(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/settings-dark.css")).toExternalForm());
                settingsScene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/settings-light.css")).toExternalForm());
            }
            case "Dark" -> {
                dashboardScene.getStylesheets().remove(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/main-light.css")).toExternalForm());
                dashboardScene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/main-dark.css")).toExternalForm());
                dashboardScene.getStylesheets().remove(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/codegalaxy-light.css")).toExternalForm());
                dashboardScene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/codegalaxy-dark.css")).toExternalForm());

                settingsScene.getStylesheets().remove(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/settings-light.css")).toExternalForm());
                settingsScene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/settings-dark.css")).toExternalForm());
            }
        }
    }


}
