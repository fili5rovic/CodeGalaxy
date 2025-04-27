package fili5rovic.codegalaxy.settings;

import fili5rovic.codegalaxy.code.manager.editing.shortcuts.keystate.KeyState;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;

public class ShortcutsTableHelper {

    public static TableView<KeyState> getShortcutsTable() {
        TableView<KeyState> keyStateTable = new TableView<>();
        keyStateTable.setEditable(true);

        TableColumn<KeyState, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getShortcutName()));
        nameColumn.setEditable(false);
        nameColumn.setReorderable(false);


        TableColumn<KeyState, KeyCode> codeColumn = new TableColumn<>("Code");
        codeColumn.setCellValueFactory(cellData -> new ObservableValueBase<>() {
            @Override
            public KeyCode getValue() {
                return cellData.getValue().getCode();
            }
        });
        codeColumn.setMaxWidth(100);
        codeColumn.setEditable(true);
        codeColumn.setReorderable(false);

        ObservableList<KeyCode> keyCodeList = FXCollections.observableArrayList(KeyCode.values());

        codeColumn.setCellFactory(_ -> new ComboBoxTableCell<KeyState, KeyCode>(keyCodeList) {
            @Override
            public void updateItem(KeyCode item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(item.getName());
                } else {
                    setText(null);
                }
            }
        });

        codeColumn.setOnEditCommit(event -> {
            KeyState keyState = event.getRowValue();
            keyState.setCode(event.getNewValue());
            ProjectSettings.getTempInstance().set("shortcut_" + keyState.getShortcutName(), keyState.toString());
        });

        TableColumn<KeyState, Boolean> ctrlColumn = new TableColumn<>("Control");
        ctrlColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isControlDown()));
        ctrlColumn.setCellFactory(CheckBoxTableCell.forTableColumn(ctrlColumn));
        ctrlColumn.setEditable(true);
        ctrlColumn.setMaxWidth(50);
        ctrlColumn.setReorderable(false);
        ctrlColumn.setOnEditCommit(event -> {
            KeyState keyState = event.getRowValue();
            keyState.setControlDown(event.getNewValue());
            ProjectSettings.getTempInstance().set("shortcut_" + keyState.getShortcutName(), keyState.toString());
        });

        TableColumn<KeyState, Boolean> shiftColumn = new TableColumn<>("Shift");
        shiftColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isShiftDown()));
        shiftColumn.setCellFactory(CheckBoxTableCell.forTableColumn(shiftColumn));
        shiftColumn.setEditable(true);
        shiftColumn.setMaxWidth(50);
        shiftColumn.setReorderable(false);
        shiftColumn.setOnEditCommit(event -> {
            KeyState keyState = event.getRowValue();
            keyState.setShiftDown(event.getNewValue());
            ProjectSettings.getTempInstance().set("shortcut_" + keyState.getShortcutName(), keyState.toString());
        });

        TableColumn<KeyState, Boolean> altColumn = new TableColumn<>("Alt");
        altColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isAltDown()));
        altColumn.setCellFactory(CheckBoxTableCell.forTableColumn(altColumn));
        altColumn.setEditable(true);
        altColumn.setMaxWidth(50);
        altColumn.setReorderable(false);
        altColumn.setOnEditCommit(event -> {
            KeyState keyState = event.getRowValue();
            keyState.setAltDown(event.getNewValue());
            ProjectSettings.getTempInstance().set("shortcut_" + keyState.getShortcutName(), keyState.toString());
        });

        keyStateTable.getColumns().addAll(nameColumn, codeColumn, ctrlColumn, shiftColumn, altColumn);

        keyStateTable.getItems().add(new KeyState("move_line_down"));
        keyStateTable.getItems().add(new KeyState("move_line_up"));
        keyStateTable.getItems().add(new KeyState("delete_line"));
        keyStateTable.getItems().add(new KeyState("duplicate_line_above"));
        keyStateTable.getItems().add(new KeyState("duplicate_line_below"));
        keyStateTable.getItems().add(new KeyState("word_select"));

        keyStateTable.setFixedCellSize(30);
        keyStateTable.prefHeightProperty().bind(Bindings.size(keyStateTable.getItems()).multiply(keyStateTable.getFixedCellSize()).add(30));
        keyStateTable.setMaxHeight(Region.USE_PREF_SIZE);

        keyStateTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        nameColumn.setMaxWidth(1000);

        ctrlColumn.setCellFactory(_ -> {
            CheckBoxTableCell<KeyState, Boolean> cell = new CheckBoxTableCell<>();
            cell.getStyleClass().add("shortcut-checkbox");
            return cell;
        });

        shiftColumn.setCellFactory(_ -> {
            CheckBoxTableCell<KeyState, Boolean> cell = new CheckBoxTableCell<>();
            cell.getStyleClass().add("shortcut-checkbox");
            return cell;
        });

        altColumn.setCellFactory(_ -> {
            CheckBoxTableCell<KeyState, Boolean> cell = new CheckBoxTableCell<>();
            cell.getStyleClass().add("shortcut-checkbox");
            return cell;
        });

        return keyStateTable;
    }
}
