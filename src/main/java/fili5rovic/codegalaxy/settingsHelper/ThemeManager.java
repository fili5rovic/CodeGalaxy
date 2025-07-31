package fili5rovic.codegalaxy.settingsHelper;

import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.util.CSSUtil;
import fili5rovic.codegalaxy.util.SVGUtil;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

public class ThemeManager {

    private static final Label lightThemeLabel = createThemeLabel("Light");
    private static final Label darkThemeLabel = createThemeLabel("Dark");

    public static Node getThemeSettingsMenuItem() {
        VBox themeSettingsMenu = new VBox();
        themeSettingsMenu.setAlignment(Pos.TOP_CENTER);
        themeSettingsMenu.setSpacing(10);

        themeSettingsMenu.getChildren().add(new Label("Editor theme"));

        ComboBox<Label> themeComboBox = new ComboBox<>();

        themeComboBox.getItems().addAll(darkThemeLabel, lightThemeLabel);

        String ideTheme = IDESettings.getInstance().get("theme");
        if (ideTheme.equals("light"))
            themeComboBox.setValue(lightThemeLabel);
        else if (ideTheme.equals("dark"))
            themeComboBox.setValue(darkThemeLabel);

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
            if (themeComboBox.getValue() == null) return;
            setTheme(themeComboBox.getValue().getText());
        });

        themeSettingsMenu.getChildren().add(themeComboBox);

        return themeSettingsMenu;
    }

    private static void setTheme(String theme) {
        if (theme != null) {
            theme = theme.toLowerCase();
            IDESettings.getInstance().set("theme", theme);
            CSSUtil.selectTheme(theme);
        }
    }

    private static Label createThemeLabel(String themeName) {
        Label label = new Label(themeName);
        label.setContentDisplay(ContentDisplay.LEFT);
        label.setGraphicTextGap(5);

        switch (themeName) {
            case "Light" -> label.setGraphic(SVGUtil.getUI("sun", 16));
            case "Dark" -> label.setGraphic(SVGUtil.getUI("moon", 16));
        }

        return label;
    }

    public static void applyThemeFromSettings() {
        String theme = IDESettings.getInstance().get("theme");
        if (theme != null && !theme.isEmpty()) {
            CSSUtil.selectTheme(theme);
        }
    }
}
