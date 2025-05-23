package fili5rovic.codegalaxy.code.manager.suggestions;

import fili5rovic.codegalaxy.util.SVGUtil;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.eclipse.lsp4j.CompletionItem;

import java.util.List;
import java.util.function.Consumer;

public class CompletionPopup extends Popup {

    private final ListView<CompletionItem> listView;
    private Consumer<CompletionItem> onItemSelected;
    private final DetailsPopup detailsPopup;

    public CompletionPopup() {
        listView = createListView();
        detailsPopup = new DetailsPopup(listView);
        VBox container = new VBox(listView);
        container.getStyleClass().add("completion-popup");
        getContent().add(container);
    }

    private ListView<CompletionItem> createListView() {
        ListView<CompletionItem> listView = new ListView<>();
        listView.getStyleClass().add("completion-list-view");
        listView.setPrefHeight(200);
        listView.setPrefWidth(300);

        listView.setCellFactory(_ -> {
            ListCell<CompletionItem> cell = new ListCell<>() {
                @Override
                protected void updateItem(CompletionItem item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        return;
                    }
                    setText(item.getLabel());
                    String svgName = item.getKind().toString().toLowerCase();
                    setGraphic(SVGUtil.getCompletionIcon(svgName, 16, 16));
                }
            };
            cell.getStyleClass().add("completion-list-cell");
            return cell;
        });

        listView.getSelectionModel().selectedItemProperty().addListener((_, _, item) -> {
            if (item != null && item.getLabel().length() > 41) {
                detailsPopup.showDetailsForItem(item);
            } else {
                detailsPopup.hide();
            }
        });

        listView.onScrollProperty().addListener((_, _, newVal) -> {
            if (newVal != null) {
                detailsPopup.hide();
            }
        });

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2)
                acceptSelectedItem();
        });

        listView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                acceptSelectedItem();
        });

        return listView;
    }


    private void acceptSelectedItem() {
        CompletionItem selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null && onItemSelected != null) {
            onItemSelected.accept(selected);
        }
        hide();
    }

    public void setSuggestions(List<CompletionItem> suggestions) {
        listView.setItems(FXCollections.observableArrayList(suggestions));
        if (!suggestions.isEmpty()) {
            listView.getSelectionModel().select(0);
        }

        double cellHeight = 32;
        int visibleRowCount = suggestions.size();
        double totalHeight = visibleRowCount * cellHeight;

        if (totalHeight > 200) {
            totalHeight = 200;
        }

        listView.setPrefHeight(totalHeight);
    }

    public void bindKeyEvents(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (isShowing()) {
                switch (event.getCode()) {
                    case ENTER:
                        CompletionItem selected = listView.getSelectionModel().getSelectedItem();
                        if (selected != null && onItemSelected != null) {
                            onItemSelected.accept(selected);
                        }
                        hide();
                        event.consume();
                        break;
                    case ESCAPE:
                        hide();
                        event.consume();
                        break;
                    case UP:
                        listView.getSelectionModel().selectPrevious();
                        listView.scrollTo(listView.getSelectionModel().getSelectedIndex());

                        event.consume();
                        break;
                    case DOWN:
                        listView.getSelectionModel().selectNext();
                        listView.scrollTo(listView.getSelectionModel().getSelectedIndex());
                        event.consume();
                        break;
                }
            }
        });
    }

    public void setOnItemSelected(Consumer<CompletionItem> handler) {
        this.onItemSelected = handler;
    }

    @Override
    public void show(Node owner, double x, double y) {
        super.show(owner, x, y);
        listView.requestFocus();
    }


}