package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.editing.shortcuts.keystate.KeyState;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class IndentForward extends Shortcut
{
    private KeyEvent event;
    public IndentForward(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    protected KeyState getKeyState() {
        return new KeyState(KeyCode.TAB);
    }

    @Override
    protected void executeSingle() {
        // empty because this is never called
        // check LineEditing.java
    }

    public void check(KeyEvent e) {
        if(validate(e)) {
            execute();
        }
    }

    @Override
    protected void executeSelection() {
        int startPar = codeGalaxy.getCaretSelectionBind().getStartParagraphIndex();
        int endPar = codeGalaxy.getCaretSelectionBind().getEndParagraphIndex();

        String selectedText = codeGalaxy.getText(startPar, 0, endPar, codeGalaxy.getText(endPar).length());

        boolean hadTab = selectedText.startsWith("\t");
        String newText = '\t' + selectedText.replaceAll("\n", "\n\t");

        int newCharNum = newText.length() - selectedText.length();
        int selectedTextStart = codeGalaxy.getCaretSelectionBind().getStartPosition();
        int selectedTextEnd = codeGalaxy.getCaretSelectionBind().getEndPosition() + newCharNum;

        selectedTextStart += 1;


        codeGalaxy.deleteText(startPar, 0, endPar, codeGalaxy.getText(endPar).length());

        codeGalaxy.insertText(startPar, 0, newText);

        codeGalaxy.selectRange(selectedTextStart, selectedTextEnd);
    }
}
