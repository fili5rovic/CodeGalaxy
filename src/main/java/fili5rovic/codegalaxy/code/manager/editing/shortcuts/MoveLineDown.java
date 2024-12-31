package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class MoveLineDown extends Shortcut {

    public MoveLineDown(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    protected boolean validate(KeyEvent e) {
        return e.getCode().equals(KeyCode.DOWN)
                && e.isControlDown() && e.isAltDown()
                && !e.isShiftDown() && !e.isMetaDown();
    }

    @Override
    protected void execute() {
        int curr = codeGalaxy.getCurrentParagraph();
        if (curr == ((ObservableList) codeGalaxy.getParagraphs()).size() - 1)
            return;

        String currText = codeGalaxy.getText(curr);
        String nextText = codeGalaxy.getText(curr + 1);

        int endColumn = nextText.length();

        codeGalaxy.replaceText(curr, 0, curr + 1, endColumn, nextText + "\n" + currText);
        codeGalaxy.moveTo(curr + 1, 0);
    }
}
