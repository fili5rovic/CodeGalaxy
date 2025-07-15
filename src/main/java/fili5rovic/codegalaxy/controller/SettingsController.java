package fili5rovic.codegalaxy.controller;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.settings.shortcut.ShortcutEntry;
import fili5rovic.codegalaxy.settings.shortcut.ShortcutsTableHelper;
import fili5rovic.codegalaxy.settingsHelper.ThemeManager;
import fili5rovic.codegalaxy.util.SVGUtil;
import fili5rovic.codegalaxy.window.Window;
import fili5rovic.codegalaxy.window.WindowHelper;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SettingsController extends ControllerBase {

    @FXML
    private TreeView<String> settingsTreeView;

    @FXML
    private BorderPane settingsPane;

    @FXML
    private Button ok;

    @FXML
    private Button apply;

    @FXML
    private Button cancel;

    private boolean shouldApplyTempSettingsLater = false;

    private TableView<ShortcutEntry> shortcutsTable;


    @Override
    public void lateInitialize(Stage stage) {
        stage.setOnShown(_ -> {
            IDESettings.copySettingsToTemp(true);
            if (shortcutsTable != null) {
                ShortcutsTableHelper.loadData(shortcutsTable);
            }
        });

        stage.setOnHidden(_ -> {
            settingsPane.setCenter(null);
            settingsTreeView.getSelectionModel().clearSelection();
        });

        stage.setOnCloseRequest(_ -> cancel());
    }

    @FXML
    public void initialize() {
        Controllers.setSettingsController(this);
        initTreeView();
        buttonActions();
    }

    private void buttonActions() {
        apply.setOnAction(_ -> {
            for (CodeGalaxy codeGalaxy : Controllers.dashboardController().getOpenCodeGalaxies()) {
                codeGalaxy.reloadShortcuts();
            }
            WindowHelper.hideWindow(Window.SETTINGS);
            shouldApplyTempSettingsLater = false;
        });

        cancel.setOnAction(_ -> cancel());

        ok.setOnAction(_ -> {
            for (CodeGalaxy codeGalaxy : Controllers.dashboardController().getOpenCodeGalaxies()) {
                codeGalaxy.reloadShortcuts();
            }
            WindowHelper.hideWindow(Window.SETTINGS);
            shouldApplyTempSettingsLater = true;
        });

        apply.setOnMouseEntered(_ -> changeBtn(apply, "perfect", ""));
        apply.setOnMouseExited(_ -> changeBtn(apply, "", "Apply"));

        ok.setOnMouseEntered(_ -> changeBtn(ok, "thumbs_up", ""));
        ok.setOnMouseExited(_ -> changeBtn(ok, "", "OK"));

        cancel.setOnMouseEntered(_ -> changeBtn(cancel, "nope", ""));
        cancel.setOnMouseExited(_ -> changeBtn(cancel, "", "Cancel"));
    }

    private void cancel() {
        IDESettings.applyTempSettings();
        ThemeManager.applyThemeFromSettings();
        WindowHelper.hideWindow(Window.SETTINGS);
        shouldApplyTempSettingsLater = false;
    }

    private void changeBtn(Button btn, String icons, String text) {
        btn.setGraphic(icons.isEmpty() ? null : SVGUtil.getEmoji(icons, 16, 16));
        btn.setText(text);
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
            case "Theme" -> ThemeManager.getThemeSettingsMenuItem();
            case "Shortcuts" -> getShortcutsSettingsMenuItem();
            default -> new Label(name);
        };
    }

    private Node getShortcutsSettingsMenuItem() {
        VBox shortcutsSettingsMenu = new VBox();
        shortcutsSettingsMenu.setAlignment(Pos.TOP_CENTER);
        shortcutsSettingsMenu.setSpacing(10);
        shortcutsSettingsMenu.getChildren().add(new Label("Code actions"));

        shortcutsTable = ShortcutsTableHelper.getShortcutsTable();

        shortcutsSettingsMenu.getChildren().add(shortcutsTable);
        return shortcutsSettingsMenu;
    }

    public boolean shouldApplyTempSettingsLater() {
        return shouldApplyTempSettingsLater;
    }
}
