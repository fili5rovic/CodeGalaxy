package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.editing.shortcuts.keystate.KeyState;
import javafx.scene.input.KeyCode;

public class IndentBackward extends Shortcut {

    public IndentBackward(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    protected KeyState getKeyState() {
        return new KeyState(KeyCode.TAB).shift();
    }

    @Override
    protected void executeSelection() {
        int startPar = codeGalaxy.getCaretSelectionBind().getStartParagraphIndex();
        int endPar = codeGalaxy.getCaretSelectionBind().getEndParagraphIndex();

        String selectedText = codeGalaxy.getText(startPar, 0, endPar, codeGalaxy.getText(endPar).length());

        boolean hadTab = selectedText.startsWith("\t");
        String newText = selectedText.replaceAll("\n\t", "\n");
        if (hadTab)
            newText = newText.substring(1);

        int newCharNum = newText.length() - selectedText.length();
        int selectedTextStart = codeGalaxy.getCaretSelectionBind().getStartPosition();
        int selectedTextEnd = codeGalaxy.getCaretSelectionBind().getEndPosition() + newCharNum;

        if (hadTab)
            selectedTextStart -= 1;


        codeGalaxy.deleteText(startPar, 0, endPar, codeGalaxy.getText(endPar).length());

        codeGalaxy.insertText(startPar, 0, newText);

        codeGalaxy.selectRange(selectedTextStart, selectedTextEnd);
    }
}
