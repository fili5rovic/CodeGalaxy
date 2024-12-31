package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import javafx.scene.input.KeyEvent;

public abstract class Shortcut {

    protected CodeGalaxy codeGalaxy;

    protected Shortcut(CodeGalaxy cg) {
        this.codeGalaxy = cg;
    }

    public void check(KeyEvent e) {
        if(validate(e)) {
            e.consume();
            execute();
        }
    }

    protected void execute() {
        if(codeGalaxy.hasSelection()) {
            executeSelection();
        } else {
            executeSingle();
        }
    }

    protected abstract boolean validate(KeyEvent e);

    protected void executeSingle() {
        executeSelection();
    }

    protected abstract void executeSelection();
}
