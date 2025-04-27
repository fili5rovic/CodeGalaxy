package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;

public class DuplicateLineAbove extends Shortcut {

    public DuplicateLineAbove(CodeGalaxy cg) {
        super(cg);
        shortcutName = "duplicate_line_above";
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