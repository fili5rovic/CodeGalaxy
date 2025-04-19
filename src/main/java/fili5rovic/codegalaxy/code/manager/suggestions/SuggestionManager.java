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

    private Popup createSuggestionPopup(List<CompletionItem> suggestions, Scene scene) {
        CompletionPopup popup = new CompletionPopup();
        popup.setSuggestions(suggestions);
        popup.setOnItemSelected(this::selectedItem);

        popup.bindKeyEvents(scene);

        return popup;
    }

    private void selectedItem(CompletionItem item) {
        int caretPosition = codeGalaxy.getCaretPosition();
        String textUpToCaret = codeGalaxy.getText(0, caretPosition);

        int wordStart = caretPosition;
        while (wordStart > 0) {
            char c = textUpToCaret.charAt(wordStart - 1);
            if (!Character.isJavaIdentifierPart(c)) {
                break;
            }
            wordStart--;
        }

        String currentWord = codeGalaxy.getText(wordStart, caretPosition);
        String insertText = item.getInsertText();
        if (insertText == null || insertText.isEmpty()) {
            insertText = item.getLabel();
        }

        if (currentWord.equals(insertText)) {
            currentPopup.hide();
            return;
        }

        codeGalaxy.replaceText(wordStart, caretPosition, insertText);
        codeGalaxy.moveTo(wordStart + insertText.length());
        codeGalaxy.requestFollowCaret();
        currentPopup.hide();
    }



    private void showPopup(CodeGalaxy codeGalaxy, List<CompletionItem> suggestions) {
        codeGalaxy.getCaretBounds().ifPresent(caretBounds -> {
            currentPopup = createSuggestionPopup(suggestions, codeGalaxy.getScene());
            currentPopup.show(
                    codeGalaxy.getScene().getWindow(),
                    caretBounds.getMinX(),
                    caretBounds.getMaxY()
            );
            currentPopup.requestFocus();
        });
    }

}