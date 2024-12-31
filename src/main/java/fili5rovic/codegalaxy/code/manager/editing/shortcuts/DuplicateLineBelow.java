package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class DuplicateLineBelow extends Shortcut {
    public DuplicateLineBelow(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    protected boolean validate(KeyEvent e) {
        return e.getCode().equals(KeyCode.DOWN)
                && e.isAltDown() && e.isControlDown()
                && !e.isShiftDown() && !e.isMetaDown();
    }

    @Override
    protected void executeSingle() {
        int curr = codeGalaxy.getCurrentParagraph();
        String text = codeGalaxy.getText(curr);

        System.out.println(curr);
        if (curr == codeGalaxy.getParagraphsCount() - 1) {
            codeGalaxy.appendText("\n" + text);
        } else {
            codeGalaxy.insertText(codeGalaxy.getAbsolutePosition(curr + 1, 0), text + "\n");
        }

        codeGalaxy.moveTo(curr + 1, codeGalaxy.getCaretColumn());

    }

    @Override
    protected void executeSelection() {

    }
}
