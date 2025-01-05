package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.editing.shortcuts.keystate.KeyState;
import javafx.scene.input.KeyCode;

public class DuplicateLineBelow extends Shortcut {
    public DuplicateLineBelow(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    protected KeyState getKeyState() {
        return new KeyState(KeyCode.DOWN).ctrl().alt();
    }

    @Override
    protected void executeSingle() {
        int curr = codeGalaxy.getCurrentParagraph();
        String text = codeGalaxy.getText(curr);

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
