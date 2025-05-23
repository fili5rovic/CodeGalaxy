package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;

public class DeleteLineShortcut extends Shortcut {

    public DeleteLineShortcut(CodeGalaxy cg) {
        super(cg);
        shortcutName = "delete_line";
    }

    @Override
    protected void executeSingle() {
        int curr = codeGalaxy.getCaretPosition();
        int lineStart = codeGalaxy.getText().lastIndexOf("\n", curr - 1) + 1;
        int lineEnd = codeGalaxy.getText().indexOf("\n", curr) + 1;

        if (lineEnd == 0) {
            lineEnd = codeGalaxy.getLength();
        }

        codeGalaxy.deleteText(lineStart, lineEnd);
    }


    @Override
    protected void executeSelection() {
        int startPar = codeGalaxy.getCaretSelectionBind().getStartParagraphIndex();
        int endPar = codeGalaxy.getCaretSelectionBind().getEndParagraphIndex();

        codeGalaxy.deleteText(startPar,0,endPar,codeGalaxy.getText(endPar).length());
        codeGalaxy.deleteNextChar();
    }
}
