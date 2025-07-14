package fili5rovic.codegalaxy.code.manager.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;

public class ShortcutActions {

    public static void indentForward(CodeGalaxy codeGalaxy) {
        if (!codeGalaxy.hasSelection())
            return;

        int startParagraph = codeGalaxy.getCaretSelectionBind().getStartParagraphIndex();
        int endParagraph = codeGalaxy.getCaretSelectionBind().getEndParagraphIndex();
        int startPosition = codeGalaxy.getCaretSelectionBind().getStartPosition();
        int endPosition = codeGalaxy.getCaretSelectionBind().getEndPosition();

        StringBuilder indentedBlock = new StringBuilder();
        int newCharsCount = 0;

        for (int i = startParagraph; i <= endParagraph; i++) {
            indentedBlock.append('\t');
            indentedBlock.append(codeGalaxy.getText(i));
            newCharsCount++;

            if (i < endParagraph) {
                indentedBlock.append('\n');
            }
        }

        codeGalaxy.replaceText(
                startParagraph, 0,
                endParagraph, codeGalaxy.getText(endParagraph).length(),
                indentedBlock.toString()
        );

        codeGalaxy.selectRange(startPosition + 1, endPosition + newCharsCount);

    }

    public static void indentBackward(CodeGalaxy codeGalaxy) {
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

        if (hadTab && selectedTextStart > 0)
            selectedTextStart -= 1;

        codeGalaxy.deleteText(startPar, 0, endPar, codeGalaxy.getText(endPar).length());

        codeGalaxy.insertText(startPar, 0, newText);

        codeGalaxy.selectRange(selectedTextStart, selectedTextEnd);
    }

    public static void deleteLine(CodeGalaxy codeGalaxy) {
        if (codeGalaxy.hasSelection()) {
            int startPar = codeGalaxy.getCaretSelectionBind().getStartParagraphIndex();
            int endPar = codeGalaxy.getCaretSelectionBind().getEndParagraphIndex();

            codeGalaxy.deleteText(startPar, 0, endPar, codeGalaxy.getText(endPar).length());
            codeGalaxy.deleteNextChar();
        } else {
            int curr = codeGalaxy.getCaretPosition();
            int lineStart = codeGalaxy.getText().lastIndexOf("\n", curr - 1) + 1;
            int lineEnd = codeGalaxy.getText().indexOf("\n", curr) + 1;

            if (lineEnd == 0) {
                lineEnd = codeGalaxy.getLength();
            }

            codeGalaxy.deleteText(lineStart, lineEnd);
        }
    }

    public static void moveLineUp(CodeGalaxy codeGalaxy) {
        int startPar = codeGalaxy.getCaretSelectionBind().getStartParagraphIndex();
        int endPar = codeGalaxy.getCaretSelectionBind().getEndParagraphIndex();

        if (startPar == 0)
            return;

        int endColumn = codeGalaxy.getText(endPar).length();

        int selectedStartColumn = codeGalaxy.getCaretSelectionBind().getStartColumnPosition();
        int selectedEndColumn = codeGalaxy.getCaretSelectionBind().getEndColumnPosition();

        String text = codeGalaxy.getText(startPar, 0, endPar, endColumn);
        // delete selected text
        codeGalaxy.deleteText(startPar, 0, endPar, endColumn);
        codeGalaxy.deletePreviousChar();
        // insert deleted text one line above
        codeGalaxy.insertText(startPar - 1, 0, text + "\n");

        int newStartPosition = codeGalaxy.getAbsolutePosition(startPar - 1, selectedStartColumn);
        int newEndPosition = codeGalaxy.getAbsolutePosition(endPar - 1, selectedEndColumn);
        codeGalaxy.selectRange(newStartPosition, newEndPosition);
    }

    public static void moveLineDown(CodeGalaxy codeGalaxy) {
        int startPar = codeGalaxy.getCaretSelectionBind().getStartParagraphIndex();
        int endPar = codeGalaxy.getCaretSelectionBind().getEndParagraphIndex();

        if (endPar == codeGalaxy.getParagraphsCount() - 1)
            return;

        int endColumn = codeGalaxy.getText(endPar).length();

        int selectedStartColumn = codeGalaxy.getCaretSelectionBind().getStartColumnPosition();
        int selectedEndColumn = codeGalaxy.getCaretSelectionBind().getEndColumnPosition();

        String text = codeGalaxy.getText(startPar, 0, endPar, endColumn);
        codeGalaxy.deleteText(startPar, 0, endPar, endColumn);
        codeGalaxy.deletePreviousChar();

        try {
            codeGalaxy.insertText(startPar + 1, 0, text + "\n");
        } catch (Exception e) {
            codeGalaxy.appendText("\n" + text);
        }

        int newStartPosition = codeGalaxy.getAbsolutePosition(startPar + 1, selectedStartColumn);
        int newEndPosition = codeGalaxy.getAbsolutePosition(endPar + 1, selectedEndColumn);
        codeGalaxy.selectRange(newStartPosition, newEndPosition);
    }

    public static void duplicateLineAbove(CodeGalaxy codeGalaxy) {
        if (!codeGalaxy.hasSelection()) {
            int curr = codeGalaxy.getCurrentParagraph();
            String text = codeGalaxy.getText(curr);

            int index = codeGalaxy.getAbsolutePosition(curr, 0);
            codeGalaxy.insertText(index, text + "\n");

            if (curr == 0)
                curr = 1;

            codeGalaxy.moveTo(curr, codeGalaxy.getCaretColumn());
        }
    }

    public static void duplicateLineBelow(CodeGalaxy codeGalaxy) {
        if (!codeGalaxy.hasSelection()) {
            int curr = codeGalaxy.getCurrentParagraph();
            String text = codeGalaxy.getText(curr);

            if (curr == codeGalaxy.getParagraphsCount() - 1) {
                codeGalaxy.appendText("\n" + text);
            } else {
                codeGalaxy.insertText(codeGalaxy.getAbsolutePosition(curr + 1, 0), text + "\n");
            }

            codeGalaxy.moveTo(curr + 1, codeGalaxy.getCaretColumn());
        }
    }

    public static void commentLine(CodeGalaxy codeGalaxy) {
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
