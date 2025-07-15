package fili5rovic.codegalaxy.code.indentation;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.Manager;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class IndentationManager extends Manager {

    public IndentationManager(CodeGalaxy cg) {
        super(cg);
    }

    private void handleEnter(KeyEvent event) {
        if (event.getCode() != KeyCode.ENTER) {
            return;
        }

        event.consume();

        int currentParagraph = codeGalaxy.getCurrentParagraph();
        String currentLineText = codeGalaxy.getParagraph(currentParagraph).getText();

        StringBuilder indent = new StringBuilder();
        for (char c : currentLineText.toCharArray()) {
            if (c == ' ' || c == '\t') {
                indent.append(c);
            } else {
                break;
            }
        }

        codeGalaxy.replaceSelection("\n" + indent);
    }

    @Override
    public void init() {
        codeGalaxy.addEventFilter(KeyEvent.KEY_PRESSED, this::handleEnter);
    }
}