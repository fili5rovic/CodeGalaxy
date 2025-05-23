package fili5rovic.codegalaxy.code.manager.suggestions;

import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.stage.Popup;
import org.eclipse.lsp4j.CompletionItem;

public class DetailsPopup extends Popup {

    private final ListView<CompletionItem> detailsListView;

    private final ListView<CompletionItem> listView;

    public DetailsPopup(ListView<CompletionItem> listView) {
        this.listView = listView;
        setAutoHide(false);

        detailsListView = new ListView<>();
        detailsListView.getStyleClass().add("details-completion-list-view");

        detailsListView.setFixedCellSize(32);
        detailsListView.setPrefHeight(32);


        detailsListView.setFocusTraversable(false);
        detailsListView.setOnMouseClicked(listView.getOnMouseClicked());
    }

    public void showDetailsForItem(CompletionItem item) {
        getContent().clear();
        detailsListView.getItems().clear();

        detailsListView.setItems(FXCollections.observableArrayList(item));

        detailsListView.setCellFactory(listView.getCellFactory());
        detailsListView.applyCss();
        detailsListView.layout();

        System.out.println("length: " + item.getLabel().length());

        detailsListView.setMinHeight(detailsListView.getFixedCellSize());

        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            javafx.application.Platform.runLater(() -> {
                Node cell = listView.lookup(".list-cell:selected");
                if (cell != null) {
                    Bounds cellBounds = cell.localToScreen(cell.getBoundsInLocal());
                    show(cell, cellBounds.getMinX(), cellBounds.getMinY());
                    double cellWidth = cell.getLayoutBounds().getWidth();
                    detailsListView.setPrefWidth(cellWidth);
                }
            });
        }
        getContent().add(detailsListView);
    }

}
