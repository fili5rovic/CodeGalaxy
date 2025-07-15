package fili5rovic.codegalaxy.code.indentation;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.code.manager.shortcuts.CodeActions;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class IndentationManager extends Manager {

    public IndentationManager(CodeGalaxy cg) {
        super(cg);
    }

    private void handleIndentation(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            preserveIndentation(e);
        }

        if (e.getCode() == KeyCode.TAB) {
            tab(e);
        }
    }

    private void tab(KeyEvent e) {
        if(codeGalaxy.hasSelection()) {
            e.consume();
            codeGalaxy.replaceSelection("\t");
        }
        if(e.isShiftDown()) {
            CodeActions.indentBackward(codeGalaxy);
        } else {
            CodeActions.indentForward(codeGalaxy);
        }
    }

    private void preserveIndentation(KeyEvent e) {
        e.consume();

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
        codeGalaxy.addEventFilter(KeyEvent.KEY_PRESSED, this::handleIndentation);
    }
}