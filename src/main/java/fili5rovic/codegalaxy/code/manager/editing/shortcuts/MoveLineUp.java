package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.editing.shortcuts.keystate.KeyState;
import javafx.scene.input.KeyCode;

public class MoveLineUp extends Shortcut {

    public MoveLineUp(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    protected KeyState getKeyState() {
        return new KeyState(KeyCode.UP).shift().alt();
    }

    @Override
    protected void executeSelection() {
        int startPar = codeGalaxy.getCaretSelectionBind().getStartParagraphIndex();
        int endPar = codeGalaxy.getCaretSelectionBind().getEndParagraphIndex();

        if(startPar == 0)
            return;

        int endColumn = codeGalaxy.getText(endPar).length();

        int selectedStartColumn = codeGalaxy.getCaretSelectionBind().getStartColumnPosition();
        int selectedEndColumn = codeGalaxy.getCaretSelectionBind().getEndColumnPosition();

        String text = codeGalaxy.getText(startPar, 0, endPar, endColumn);
        // delete selected text
        codeGalaxy.deleteText(startPar, 0, endPar, endColumn);
        codeGalaxy.deletePreviousChar();
        // insert deleted text one line above
        codeGalaxy.insertText(startPar-1, 0, text + "\n");

        int newStartPosition = codeGalaxy.getAbsolutePosition(startPar - 1, selectedStartColumn);
        int newEndPosition = codeGalaxy.getAbsolutePosition(endPar - 1, selectedEndColumn);
        codeGalaxy.selectRange(newStartPosition, newEndPosition);
    }
}
