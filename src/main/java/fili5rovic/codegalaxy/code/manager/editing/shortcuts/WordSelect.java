package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class WordSelect extends Shortcut{
    public WordSelect(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    protected boolean validate(KeyEvent e) {
        return e.getCode().equals(KeyCode.W)
                && e.isControlDown()
                && !e.isShiftDown() && !e.isAltDown() && !e.isMetaDown();
    }

    @Override
    protected void executeSelection() {
        codeGalaxy.selectWord();
    }
}
