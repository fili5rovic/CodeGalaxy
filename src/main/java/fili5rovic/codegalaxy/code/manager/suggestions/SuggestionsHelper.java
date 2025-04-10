package fili5rovic.codegalaxy.code.manager.suggestions;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

public class SuggestionsHelper {

    private static MenuBar createSuggestionMenu() {
        Menu suggestion1 = new Menu("s");
        return new MenuBar(suggestion1);
    }

    private static final Popup currentPopup = new Popup();

    public static void suggestion(CodeGalaxy codeGalaxy) {
        codeGalaxy.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
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
        });

        codeGalaxy.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                currentPopup.hide();
            }
        });
    }
}