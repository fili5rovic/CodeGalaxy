package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;

public class MoveLineDown extends Shortcut {
    public MoveLineDown(CodeGalaxy cg) {
        super(cg);
        shortcutName = "move_line_down";
    }

    @Override
    protected void executeSelection() {
        int startPar = codeGalaxy.getCaretSelectionBind().getStartParagraphIndex();
        int endPar = codeGalaxy.getCaretSelectionBind().getEndParagraphIndex();

        if(endPar == codeGalaxy.getParagraphsCount() - 1)
            return;

        int endColumn = codeGalaxy.getText(endPar).length();

        int selectedStartColumn = codeGalaxy.getCaretSelectionBind().getStartColumnPosition();
        int selectedEndColumn = codeGalaxy.getCaretSelectionBind().getEndColumnPosition();

        String text = codeGalaxy.getText(startPar, 0, endPar, endColumn);
        codeGalaxy.deleteText(startPar, 0, endPar, endColumn);
        codeGalaxy.deletePreviousChar();

        try {
            codeGalaxy.insertText(startPar+1, 0, text + "\n");
        } catch (Exception e) {
            codeGalaxy.appendText("\n" + text);
        }

        int newStartPosition = codeGalaxy.getAbsolutePosition(startPar + 1, selectedStartColumn);
        int newEndPosition = codeGalaxy.getAbsolutePosition(endPar + 1, selectedEndColumn);
        codeGalaxy.selectRange(newStartPosition, newEndPosition);
    }
}
