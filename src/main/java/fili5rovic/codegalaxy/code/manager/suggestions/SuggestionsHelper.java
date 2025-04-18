package fili5rovic.codegalaxy.code.manager.suggestions;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.lsp.LSPManager;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.fxmisc.richtext.model.TwoDimensional;

public class SuggestionsHelper {

    private static int line = 0;
    private static int column = 0;

    private static final Popup currentPopup = new Popup();

    private static MenuBar createSuggestionMenu() {
        Menu suggestion1 = new Menu("s");
        return new MenuBar(suggestion1);
    }

    private static void showPopup(CodeGalaxy codeGalaxy) {
        codeGalaxy.requestLayout();

        codeGalaxy.getCaretBounds().ifPresent(caretBounds -> {
            VBox suggestionPane = new VBox(createSuggestionMenu());
            currentPopup.getContent().clear();
            currentPopup.getContent().add(suggestionPane);

            currentPopup.show(
                    codeGalaxy.getScene().getWindow(),
                    caretBounds.getMinX(),
                    caretBounds.getMaxY()
            );
        });
    }


    public static void suggestion(CodeGalaxy codeGalaxy) {
        codeGalaxy.caretPositionProperty().addListener((obs, oldVal, newVal) -> {
            int offset = codeGalaxy.getCaretPosition();
            TwoDimensional.Position pos = codeGalaxy.offsetToPosition(offset, TwoDimensional.Bias.Forward);
            line = pos.getMajor();
            column = Math.max(pos.getMinor() - 1, 0);
            System.out.println("Line: " + line + ", Column: " + column);
            currentPopup.hide();
        });

        codeGalaxy.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.SPACE && event.isControlDown()) {
                showPopup(codeGalaxy);

                try {
                    LSPManager.getInstance().requestCompletions(codeGalaxy.getFilePath().toString(), line, column);
                } catch (Exception e) {
                    System.out.println("Failed to request completions: " + e.getMessage());
                }
                event.consume();
            }
        });

        codeGalaxy.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                currentPopup.hide();
            }
        });
    }
}