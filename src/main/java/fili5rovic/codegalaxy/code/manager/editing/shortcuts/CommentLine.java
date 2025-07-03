package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;

import java.security.InvalidParameterException;

public class CommentLine extends Shortcut {

    public CommentLine(CodeGalaxy cg) {
        super(cg);
        shortcutName = "comment_line";
    }

    @Override
    protected void executeSelection() {
        int startPar = codeGalaxy.getCaretSelectionBind().getStartParagraphIndex();
        int endPar = codeGalaxy.getCaretSelectionBind().getEndParagraphIndex();

        int finalCaretLine = -1;
        int finalCaretCol = -1;

        for (int i = startPar; i <= endPar; i++) {
            String line = codeGalaxy.getText(i);
            int caretColumn = codeGalaxy.getCaretColumn();

            if (line.startsWith("//")) {
                codeGalaxy.deleteText(i, 0, i, 2);
            } else {
                codeGalaxy.insertText(i, 0, "//");
                if (i + 1 < codeGalaxy.getParagraphsCount()) {
                    finalCaretLine = i + 1;
                    finalCaretCol = caretColumn + 2;
                } else {
                    finalCaretLine = i;
                    finalCaretCol = Math.min(caretColumn + 2, codeGalaxy.getText(i).length());
                }
            }
        }
        if (finalCaretLine != -1) {
            String targetLineText = codeGalaxy.getText(finalCaretLine);
            int maxCol = targetLineText.length();
            int safeCaretCol = Math.min(finalCaretCol, maxCol);
            codeGalaxy.moveTo(finalCaretLine, safeCaretCol);
        }
    }
}
