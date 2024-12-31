package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ExitShortcut extends Shortcut{
    public ExitShortcut(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    protected boolean validate(KeyEvent e) {
        return e.getCode().equals(KeyCode.ESCAPE)
                && !e.isControlDown() && !e.isShiftDown()
                && !e.isAltDown() && !e.isMetaDown();
    }


    @Override
    protected void executeSelection() {
        Platform.exit();
    }
}
