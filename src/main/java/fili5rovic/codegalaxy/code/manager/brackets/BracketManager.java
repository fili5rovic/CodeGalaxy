package fili5rovic.codegalaxy.code.manager.brackets;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.Manager;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Map;

public class BracketManager extends Manager {

    private static final Map<String, String> BRACKET_PAIRS = Map.of(
            "(", ")",
            "[", "]",
            "{", "}"
    );

    public BracketManager(CodeGalaxy cg) {
        super(cg);
    }

    private void bracketCompletion(KeyEvent event) {
        String typedChar = event.getCharacter();

        if (BRACKET_PAIRS.containsKey(typedChar)) {
            event.consume();

            String closingBracket = BRACKET_PAIRS.get(typedChar);

            if (codeGalaxy.hasSelection()) {
                String selectedText = codeGalaxy.getSelectedText();
                codeGalaxy.replaceSelection(typedChar + selectedText + closingBracket);
            } else {
                int caretPosition = codeGalaxy.getCaretPosition();
                codeGalaxy.insertText(caretPosition, typedChar + closingBracket);
                codeGalaxy.moveTo(caretPosition + 1);
            }
        }
    }

    private void handleBackspace(KeyEvent event) {
        if (event.getCode() != KeyCode.BACK_SPACE) {
            return;
        }

        if (codeGalaxy.hasSelection() || codeGalaxy.getCaretPosition() == 0) {
            return;
        }

        if (codeGalaxy.getCaretPosition() >= codeGalaxy.getLength()) {
            return;
        }

        int caretPosition = codeGalaxy.getCaretPosition();

        String charBefore = codeGalaxy.getText(caretPosition - 1, caretPosition);
        String charAfter = codeGalaxy.getText(caretPosition, caretPosition + 1);

        if (BRACKET_PAIRS.containsKey(charBefore) && BRACKET_PAIRS.get(charBefore).equals(charAfter)) {
            event.consume();
            codeGalaxy.deleteText(caretPosition - 1, caretPosition + 1);
        }
    }


    @Override
    public void init() {
        codeGalaxy.addEventFilter(KeyEvent.KEY_TYPED, this::bracketCompletion);
        codeGalaxy.addEventFilter(KeyEvent.KEY_PRESSED, this::handleBackspace);
    }
}