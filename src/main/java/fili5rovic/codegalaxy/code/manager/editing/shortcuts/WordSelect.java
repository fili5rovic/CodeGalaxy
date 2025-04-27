package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;

public class WordSelect extends Shortcut {

    public WordSelect(CodeGalaxy cg) {
        super(cg);
        shortcutName = "word_select";
    }

    @Override
    protected void executeSelection() {
        codeGalaxy.selectWord();
    }
}
