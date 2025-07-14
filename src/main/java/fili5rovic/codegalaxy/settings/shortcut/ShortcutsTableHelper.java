package fili5rovic.codegalaxy.settings.shortcut;

import fili5rovic.codegalaxy.settings.IDESettings;
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
import javafx.scene.paint.Color;
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

        ObservableList<ShortcutEntry> items = FXCollections.observableArrayList();

        IDESettings settings = IDESettings.getInstance();
        String[] keys = settings.getShortcutKeys();
        for (String key : keys) {
            items.add(new ShortcutEntry(key, settings.get(key)));
        }

        table.setItems(items);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return table;
    }

    private static Stage createShortcutRecordingDialog(ShortcutEntry entry) {
        Stage dialog = new Stage(StageStyle.UTILITY);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Record Shortcut");

        // --- UI Elements ---
        Label instruction = new Label("Press shortcut for: " + entry.getName());
        Label recordedShortcutLabel = new Label("Press keys...");
        recordedShortcutLabel.setStyle("-fx-font-weight: bold; -fx-background-color: #eee; -fx-padding: 8px; -fx-border-color: #ccc; -fx-border-width: 1px; -fx-min-width: 280;");
        recordedShortcutLabel.setAlignment(Pos.CENTER);

        Label validationLabel = new Label();
        validationLabel.setTextFill(Color.RED);

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

        // --- Logic ---
        final Set<KeyCode> currentlyPressed = new HashSet<>();
        final Set<KeyCode> finalCombination = new HashSet<>();
        final boolean[] isRecording = {false}; // Use an array to be modifiable in lambda

        scene.setOnKeyPressed((KeyEvent event) -> {
            // If not currently recording a sequence, clear previous attempt
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

            // When all keys are released, the recording sequence is over
            if (currentlyPressed.isEmpty()) {
                isRecording[0] = false;
                // Update display with the last valid recorded combination
                updateDisplay(finalCombination, recordedShortcutLabel, validationLabel, okButton);
            }
            event.consume();
        });

        dialog.focusedProperty().addListener((_, _, newVal) -> {
            if (!newVal) { // If window loses focus, reset everything
                currentlyPressed.clear();
                finalCombination.clear();
                isRecording[0] = false;
                updateDisplay(finalCombination, recordedShortcutLabel, validationLabel, okButton);
            }
        });

        okButton.setOnAction(_ -> {
            String shortcutText = formatShortcut(finalCombination);
            IDESettings.getInstance().set(entry.getName(), shortcutText);
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
}