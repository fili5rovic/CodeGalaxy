package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.editing.shortcuts.keystate.KeyState;
import javafx.scene.input.KeyCode;

public class DuplicateLineAbove extends Shortcut {
    public DuplicateLineAbove(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    protected KeyState getKeyState() {
        return new KeyState(KeyCode.UP).ctrl().alt();
    }

    @Override
    protected void executeSingle() {
        int curr = codeGalaxy.getCurrentParagraph();
        String text = codeGalaxy.getText(curr);

        int index = codeGalaxy.getAbsolutePosition(curr, 0);
        codeGalaxy.insertText(index, text + "\n");

        if(curr == 0)
            curr = 1;

        codeGalaxy.moveTo(curr, codeGalaxy.getCaretColumn());

    }



    @Override
    protected void executeSelection() {

    }
}