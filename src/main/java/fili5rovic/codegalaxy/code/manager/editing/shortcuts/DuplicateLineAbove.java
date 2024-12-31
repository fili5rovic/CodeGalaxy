package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class DuplicateLineAbove extends Shortcut {
    public DuplicateLineAbove(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    protected boolean validate(KeyEvent e) {
        return e.getCode().equals(KeyCode.UP)
                && e.isAltDown() && !e.isShiftDown()
                && !e.isControlDown() && !e.isMetaDown();
    }

    @Override
    protected void execute() {
        int curr = codeGalaxy.getCurrentParagraph();
        String text = codeGalaxy.getText(curr);

        int index = codeGalaxy.getAbsolutePosition(curr, 0);
        codeGalaxy.insertText(index, text + "\n");
        if(curr == 0)
            curr = 1;

        codeGalaxy.moveTo(curr-1, codeGalaxy.getCaretColumn());

    }
}