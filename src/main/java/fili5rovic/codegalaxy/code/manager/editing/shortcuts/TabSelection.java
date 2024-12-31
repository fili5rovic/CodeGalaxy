package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class TabSelection extends Shortcut
{
    private KeyEvent event;
    public TabSelection(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    protected boolean validate(KeyEvent e) {
        this.event = e;
        return e.getCode().equals(KeyCode.TAB)
                && !e.isControlDown()
                && !e.isAltDown() && !e.isMetaDown();
    }

    @Override
    protected void executeSingle() {
        // empty because this is never called
        // check LineEditing.java
    }

    @Override
    protected void executeSelection() {
        int startPar = codeGalaxy.getCaretSelectionBind().getStartParagraphIndex();
        int endPar = codeGalaxy.getCaretSelectionBind().getEndParagraphIndex();

        String selectedText = codeGalaxy.getText(startPar, 0, endPar, codeGalaxy.getText(endPar).length());

        String newText;
        boolean hadTab = selectedText.startsWith("\t");
        if(event.isShiftDown()) {
            newText = selectedText.replaceAll("\n\t", "\n");
            if(hadTab) {
                newText = newText.substring(1);
            }
        } else {
            newText = '\t' + selectedText.replaceAll("\n", "\n\t");
        }
        int newCharNum = newText.length() - selectedText.length();
        int selectedTextStart = codeGalaxy.getCaretSelectionBind().getStartPosition();
        int selectedTextEnd = codeGalaxy.getCaretSelectionBind().getEndPosition() + newCharNum;

        if(event.isShiftDown()) {
            if(hadTab)
                selectedTextStart -= 1;
        } else {
            selectedTextStart += 1;
        }

        codeGalaxy.deleteText(startPar, 0, endPar, codeGalaxy.getText(endPar).length());

        codeGalaxy.insertText(startPar, 0, newText);

        codeGalaxy.selectRange(selectedTextStart, selectedTextEnd);
    }
}
