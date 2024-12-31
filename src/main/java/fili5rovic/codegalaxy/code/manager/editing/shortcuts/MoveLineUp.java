package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class MoveLineUp extends Shortcut {

    public MoveLineUp(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    protected boolean validate(KeyEvent e) {
        return e.getCode().equals(KeyCode.UP)
                && e.isControlDown() && e.isAltDown()
                && !e.isShiftDown() && !e.isMetaDown();
    }

    @Override
    protected void execute() {
        int curr = codeGalaxy.getCurrentParagraph();
        if(curr == 0)
            return;

        String currText = codeGalaxy.getText(curr);
        String prevText = codeGalaxy.getText(curr-1);
        int endColumn = currText.length();

        codeGalaxy.replaceText(curr-1,0,curr,endColumn, currText + "\n" + prevText);
        codeGalaxy.moveTo(curr-1, 0);
    }
}
