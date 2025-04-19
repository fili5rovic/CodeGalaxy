package fili5rovic.codegalaxy.code.manager.suggestions;

import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import org.eclipse.lsp4j.CompletionItem;

import java.util.List;
import java.util.function.Consumer;

public class CompletionPopup extends Popup {

    private final ListView<CompletionItem> listView;
    private Consumer<CompletionItem> onItemSelected;
    private Popup detailsPopup;



    public CompletionPopup() {
        listView = new ListView<>();
        listView.getStyleClass().add("completion-list-view");
        listView.setPrefHeight(200);
        listView.setPrefWidth(300);

        detailsPopup = new Popup();
        detailsPopup.setAutoHide(false);

        listView.setCellFactory(_ -> {
            ListCell<CompletionItem> cell = new ListCell<>() {
                @Override
                protected void updateItem(CompletionItem item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item.getLabel());
                        setGraphic(null);
                    }
                }
            };
            cell.getStyleClass().add("completion-list-cell");
            return cell;
        });
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showDetailsPopup(newSelection);
            } else {
                detailsPopup.hide();
            }
        });

        showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (!isShowing) {
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

        VBox container = new VBox(listView);
        container.getStyleClass().add("completion-popup");
        getContent().add(container);
    }

    private void showDetailsPopup(CompletionItem item) {
        VBox detailsContent = new VBox();
        detailsContent.getStyleClass().add("completion-details-content");
        detailsContent.setStyle("-fx-background-color: #3c3f41; -fx-padding: 10px; -fx-border-color: #5e5e5e;");

        Label titleLabel = new Label(item.getLabel());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #cccccc;");

        Label detailLabel = null;
        if (item.getDetail() != null && !item.getDetail().isEmpty()) {
            detailLabel = new Label(item.getDetail());
            detailLabel.setStyle("-fx-text-fill: #9cdcfe;");
        }

        Label docLabel = null;
        if (item.getDocumentation() != null && !item.getDocumentation().toString().isEmpty()) {
            docLabel = new Label(item.getDocumentation().toString());
            docLabel.setStyle("-fx-text-fill: #cccccc; -fx-wrap-text: true;");
        }

        detailsContent.getChildren().add(titleLabel);
        if (detailLabel != null) detailsContent.getChildren().add(detailLabel);
        if (docLabel != null) detailsContent.getChildren().add(docLabel);

        // clear and set new content
        detailsPopup.getContent().clear();
        detailsPopup.getContent().add(detailsContent);

        IndexedCell<?> cell = (IndexedCell<?>) listView.lookup(".list-cell:selected");
        if (cell != null && cell.isVisible()) {
            Node container = getContent().get(0);
            Bounds mainPopupBounds = container.localToScreen(container.getBoundsInLocal());

            double x = mainPopupBounds.getMinX() + container.getBoundsInLocal().getWidth() + 5; // 5px gap

            Bounds cellBounds = cell.localToScreen(cell.getBoundsInLocal());
            double y = cellBounds.getMinY();

            detailsContent.setMaxWidth(300);

            detailsPopup.show(getOwnerWindow(), x, y);
        }
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

        // Show details for initial selection if any
        CompletionItem selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showDetailsPopup(selected);
        }
    }

    @Override
    public void hide() {
        detailsPopup.hide();
        super.hide();
    }

    private Window getOwnerWindowHelper() {
        if (!getContent().isEmpty()) {
            Scene scene = getContent().get(0).getScene();
            if (scene != null) {
                return scene.getWindow();
            }
        }
        return null;
    }
}