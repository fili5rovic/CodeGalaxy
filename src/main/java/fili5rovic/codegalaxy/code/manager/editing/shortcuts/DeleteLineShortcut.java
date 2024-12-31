package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import javafx.scene.control.IndexRange;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class DeleteLineShortcut extends Shortcut {
    public DeleteLineShortcut(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    protected boolean validate(KeyEvent e) {
        return e.getCode().equals(KeyCode.D)
                && e.isControlDown() && !e.isShiftDown()
                && !e.isAltDown() && !e.isMetaDown();
    }

    @Override
    protected void execute() {
        int curr = codeGalaxy.getCaretPosition();
        String textBefore = codeGalaxy.getText(new IndexRange(0, curr));
        String textAfter = codeGalaxy.getText(new IndexRange(curr, codeGalaxy.getLength()));

        int start = textBefore.lastIndexOf("\n") + 1;
        int end = textAfter.indexOf("\n") + curr + 1;

        codeGalaxy.deleteText(start, end);
    }
}
