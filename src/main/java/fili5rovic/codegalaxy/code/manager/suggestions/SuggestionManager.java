package fili5rovic.codegalaxy.code.manager.suggestions;

import fili5rovic.codegalaxy.Main;
import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.lsp.Debouncer;
import fili5rovic.codegalaxy.lsp.LSPManager;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.eclipse.lsp4j.CompletionItem;
import org.fxmisc.richtext.model.TwoDimensional;

import java.util.ArrayList;
import java.util.List;

public class SuggestionManager extends Manager {

    private int line = 0;
    private int column = 0;

    private Popup currentPopup;

    private List<CompletionItem> items;

    public SuggestionManager(CodeGalaxy cg) {
        super(cg);
        currentPopup = null;
        items = null;
    }


    @Override
    public void init() {
        codeGalaxy.caretPositionProperty().addListener((obs, oldVal, newVal) -> {
            int offset = codeGalaxy.getCaretPosition();
            TwoDimensional.Position pos = codeGalaxy.offsetToPosition(offset, TwoDimensional.Bias.Forward);
            line = pos.getMajor();
            column = pos.getMinor();
            System.out.println("Line: " + line + ", Column: " + column);
            if (currentPopup != null) {
                currentPopup.hide();
            }
        });

        codeGalaxy.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.SPACE && event.isControlDown()) {
                try {
                    Debouncer debouncer = LSPManager.getInstance().getDebouncer();
                    if (debouncer.isDebouncing()) {
                        debouncer.cancel();
                        LSPManager.getInstance().sendChange(
                                codeGalaxy.getFilePath().toString(),
                                codeGalaxy.getText()
                        );
                    }
                    items = LSPManager.getInstance().requestCompletions(codeGalaxy.getFilePath().toString(), line, column);
                    if (items != null && !items.isEmpty())
                        showPopup(codeGalaxy, items);

                } catch (Exception e) {
                    System.out.println("Failed to request completions: " + e.getMessage());
                }
                event.consume();
            }
        });

        codeGalaxy.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && currentPopup != null) {
                currentPopup.hide();
            }
        });
    }

    private Popup createSuggestionPopup(List<CompletionItem> suggestions, Node owner, Scene scene) {
        Popup popup = new Popup();

        VBox content = new VBox();
        content.setStyle("-fx-background-color: #2e2e2e; -fx-padding: 8; -fx-background-radius: 6;");

        List<Label> labels = new ArrayList<>();
        final int[] selectedIndex = { -1 };

        for (CompletionItem suggestion : suggestions) {
            Label label = new Label(suggestion.getLabel());
            label.setStyle("-fx-text-fill: white; -fx-padding: 4 8; -fx-font-size: 14;");
            label.setOnMouseClicked(event -> {
                System.out.println("Selected: " + suggestion.getLabel());
                popup.hide();
            });
            labels.add(label);
            content.getChildren().add(label);
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(200);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: #2e2e2e; -fx-background-color: #2e2e2e;");

        popup.getContent().add(scrollPane);

        // Handle key events
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (popup.isShowing()) {
                switch (event.getCode()) {
                    case DOWN:
                        if (selectedIndex[0] < labels.size() - 1) {
                            selectedIndex[0]++;
                            updateSelection(labels, selectedIndex[0]);
                            ensureVisible(scrollPane, labels.get(selectedIndex[0]));
                        }
                        event.consume();
                        break;
                    case UP:
                        if (selectedIndex[0] > 0) {
                            selectedIndex[0]--;
                            updateSelection(labels, selectedIndex[0]);
                            ensureVisible(scrollPane, labels.get(selectedIndex[0]));
                        }
                        event.consume();
                        break;
                    case ENTER:
                        if (selectedIndex[0] != -1) {
                            System.out.println("Selected: " + labels.get(selectedIndex[0]).getText());
                            popup.hide();
                        }
                        event.consume();
                        break;
                    case ESCAPE:
                        popup.hide();
                        event.consume();
                        break;
                }
            }
        });

        return popup;
    }

    private void updateSelection(List<Label> labels, int selectedIndex) {
        for (int i = 0; i < labels.size(); i++) {
            if (i == selectedIndex) {
                labels.get(i).setStyle("-fx-background-color: #454545; -fx-text-fill: white; -fx-padding: 4 8; -fx-font-size: 14;");
            } else {
                labels.get(i).setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-padding: 4 8; -fx-font-size: 14;");
            }
        }
    }

    private void ensureVisible(ScrollPane scrollPane, Label label) {
        double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
        double scrollPaneHeight = scrollPane.getViewportBounds().getHeight();
        double y = label.getBoundsInParent().getMinY();

        double vValue = (y) / (contentHeight - scrollPaneHeight);
        scrollPane.setVvalue(vValue);
    }

    private void showPopup(CodeGalaxy codeGalaxy, List<CompletionItem> suggestions) {
        codeGalaxy.getCaretBounds().ifPresent(caretBounds -> {
            currentPopup = createSuggestionPopup(suggestions, codeGalaxy, codeGalaxy.getScene());
            currentPopup.show(
                    codeGalaxy.getScene().getWindow(),
                    caretBounds.getMinX(),
                    caretBounds.getMaxY()
            );
            currentPopup.requestFocus();
        });
    }

}