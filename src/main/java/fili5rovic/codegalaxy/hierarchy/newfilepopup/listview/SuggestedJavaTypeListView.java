package fili5rovic.codegalaxy.hierarchy.newfilepopup.listview;

import fili5rovic.codegalaxy.util.SVGUtil;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

public class SuggestedJavaTypeListView extends ListView<ItemEntry> {

    public SuggestedJavaTypeListView() {
        super();
        this.getStyleClass().add("suggested-java-type-list-view");
        data();
        listeners();
    }

    private void listeners() {
        this.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(ItemEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    HBox hbox = new HBox();
                    hbox.getChildren().add(item.icon());
                    hbox.getChildren().add(new Label(item.label()));
                    hbox.setSpacing(10);
                    setGraphic(hbox);
                }
            }
        });

        this.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            int size = getItems().size();
            if (size == 0) return;

            int selected = getSelectionModel().getSelectedIndex();

            if (e.getCode() == KeyCode.UP && selected == 0) {
                getSelectionModel().selectLast();
                e.consume();
            } else if (e.getCode() == KeyCode.DOWN && selected == size - 1) {
                getSelectionModel().selectFirst();
                e.consume();
            }
        });

        // double click should create a new file with the selected type



    }

    private void data() {
        this.getItems().addAll(
                new ItemEntry("Class", SVGUtil.getCompletionIcon("class", 16, 16)),
                new ItemEntry("Interface", SVGUtil.getCompletionIcon("interface", 16, 16)),
                new ItemEntry("Record", SVGUtil.getCompletionIcon("record", 16, 16)),
                new ItemEntry("Enum", SVGUtil.getCompletionIcon("enum", 16, 16)),
                new ItemEntry("Annotation", SVGUtil.getCompletionIcon("annotation", 16, 16)),
                new ItemEntry("Exception", SVGUtil.getCompletionIcon("exception", 16, 16))
        );

        getSelectionModel().selectFirst();
    }
}
