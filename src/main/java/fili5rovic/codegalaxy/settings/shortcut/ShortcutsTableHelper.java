package fili5rovic.codegalaxy.settings.shortcut;

import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.util.CSSUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ShortcutsTableHelper {

    public static TableView<ShortcutEntry> getShortcutsTable() {
        TableView<ShortcutEntry> table = new TableView<>();

        TableColumn<ShortcutEntry, String> nameCol = new TableColumn<>("Shortcut");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        nameCol.setEditable(false);

        TableColumn<ShortcutEntry, String> valueCol = new TableColumn<>("Keys");
        valueCol.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        valueCol.setEditable(true);

        valueCol.setCellFactory(_ -> new TableCell<>() {
            @Override
            public void startEdit() {
                super.startEdit();
                if (getTableRow() != null && getTableRow().getItem() != null) {
                    ShortcutEntry entry = getTableRow().getItem();
                    Stage dialog = createShortcutRecordingDialog(entry);
                    dialog.showAndWait();
                }
                cancelEdit();
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
            }
        });

        table.getColumns().add(nameCol);
        table.getColumns().add(valueCol);
        table.setEditable(true);

        loadData(table);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return table;
    }

    public static void loadData(TableView<ShortcutEntry> table) {
        ObservableList<ShortcutEntry> items = FXCollections.observableArrayList();

        String[] keys = IDESettings.getInstance().getShortcutKeys();
        for (String key : keys) {
            System.out.println(prettyShortcutName(key) + " = " + IDESettings.getInstance().get(key));
            items.add(new ShortcutEntry(prettyShortcutName(key), IDESettings.getInstance().get(key)));
        }
        table.setItems(items);
    }

    private static Stage createShortcutRecordingDialog(ShortcutEntry entry) {
        Stage dialog = new Stage(StageStyle.UTILITY);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Record Shortcut");

        Label instruction = new Label("Recording shortcut: " + prettyShortcutName(entry.getName()));
        Label recordedShortcutLabel = new Label("Press keys...");
        recordedShortcutLabel.getStyleClass().clear();
        recordedShortcutLabel.getStyleClass().add("recorded-shortcut-label");
        recordedShortcutLabel.setAlignment(Pos.CENTER);

        Label validationLabel = new Label();
        validationLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        Button okButton = new Button("OK");
        okButton.setDisable(true);
        Button cancelButton = new Button("Cancel");

        HBox buttonBox = new HBox(10, okButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(10, instruction, recordedShortcutLabel, validationLabel, buttonBox);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 380, 200);
        dialog.setScene(scene);

        scene.getStylesheets().clear();
        CSSUtil.applyStylesheet(scene.getStylesheets(), "settings");

        final Set<KeyCode> currentlyPressed = new HashSet<>();
        final Set<KeyCode> finalCombination = new HashSet<>();
        final boolean[] isRecording = {false};

        scene.setOnKeyPressed((KeyEvent event) -> {
            if (!isRecording[0]) {
                currentlyPressed.clear();
                finalCombination.clear();
                isRecording[0] = true;
            }

            currentlyPressed.add(event.getCode());
            updateDisplay(currentlyPressed, recordedShortcutLabel, validationLabel, okButton);
            event.consume();
        });

        scene.setOnKeyReleased((KeyEvent event) -> {
            if (isValidCombination(currentlyPressed)) {
                finalCombination.clear();
                finalCombination.addAll(currentlyPressed);
            }

            currentlyPressed.remove(event.getCode());

            if (currentlyPressed.isEmpty()) {
                isRecording[0] = false;
                updateDisplay(finalCombination, recordedShortcutLabel, validationLabel, okButton);
            }
            event.consume();
        });

        dialog.focusedProperty().addListener((_, _, newVal) -> {
            if (!newVal) {
                currentlyPressed.clear();
                finalCombination.clear();
                isRecording[0] = false;
                updateDisplay(finalCombination, recordedShortcutLabel, validationLabel, okButton);
            }
        });

        okButton.setOnAction(_ -> {
            String shortcutText = formatShortcut(finalCombination);
            IDESettings.getInstance().set(reversePrettyShortcutName(entry.getName()), shortcutText);
            entry.setValue(shortcutText);
            dialog.close();
        });

        cancelButton.setOnAction(_ -> dialog.close());

        root.requestFocus();
        return dialog;
    }

    private static void updateDisplay(Set<KeyCode> keys, Label shortcutLabel, Label validationLabel, Button okButton) {
        if (keys.isEmpty()) {
            shortcutLabel.setText("Press keys...");
            validationLabel.setText("");
            okButton.setDisable(true);
            return;
        }

        shortcutLabel.setText(formatShortcut(keys));

        if (isValidCombination(keys)) {
            validationLabel.setText("");
            okButton.setDisable(false);
        } else {
            validationLabel.setText("Invalid: Must contain one main key (e.g., A, B, F1).");
            okButton.setDisable(true);
        }
    }

    private static boolean isValidCombination(Set<KeyCode> keys) {
        if (keys.isEmpty()) {
            return false;
        }
        long mainKeyCount = keys.stream().filter(k -> !isModifier(k)).count();
        return mainKeyCount == 1;
    }

    private static boolean isModifier(KeyCode code) {
        return code == KeyCode.CONTROL || code == KeyCode.SHIFT || code == KeyCode.ALT || code == KeyCode.META;
    }

    private static String formatShortcut(Set<KeyCode> keys) {
        if (keys.isEmpty()) {
            return "";
        }
        return keys.stream()
                .map(KeyCode::getName)
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.joining("+"));
    }

    private static String prettyShortcutName(String name) {
        return name.replace("shortcut_", "").replace("_", " ").toUpperCase();
    }

    private static String reversePrettyShortcutName(String name) {
        return "shortcut_" + name.toLowerCase().replace(" ", "_");
    }
}