package fili5rovic.codegalaxy.util;

import fili5rovic.codegalaxy.window.Window;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

public class SettingsMenuHelper {

    public static Node getSettingsMenuItem(String name) {
        return switch (name) {
            case "Theme" -> getThemeSettingsMenuItem();
            default -> new Label(name);
        };
    }

    private static Node getThemeSettingsMenuItem() {
        VBox themeSettingsMenu = new VBox();
        themeSettingsMenu.setAlignment(Pos.TOP_CENTER);
        themeSettingsMenu.setSpacing(10);

        themeSettingsMenu.getChildren().add(new Label("Theme Settings"));

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
        switch (theme) {
            case "Light" -> {
                Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage().getScene().getStylesheets().clear();
                Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage().getScene().getStylesheets().add("fili5rovic/codegalaxy/css/main-light.css");
                Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage().getScene().getStylesheets().add("fili5rovic/codegalaxy/css/codegalaxy-light.css");

                Window.getWindowAt(Window.SETTINGS).getStage().getScene().getStylesheets().clear();
                Window.getWindowAt(Window.SETTINGS).getStage().getScene().getStylesheets().add("fili5rovic/codegalaxy/css/settings-light.css");
            }
            case "Dark" -> {
                Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage().getScene().getStylesheets().clear();
                Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage().getScene().getStylesheets().add("fili5rovic/codegalaxy/css/main-dark.css");
                Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage().getScene().getStylesheets().add("fili5rovic/codegalaxy/css/codegalaxy-dark.css");

                Window.getWindowAt(Window.SETTINGS).getStage().getScene().getStylesheets().clear();
                Window.getWindowAt(Window.SETTINGS).getStage().getScene().getStylesheets().add("fili5rovic/codegalaxy/css/settings-dark.css");
            }
        }
    }

}
