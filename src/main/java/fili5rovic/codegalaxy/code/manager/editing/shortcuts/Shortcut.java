package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import javafx.scene.input.KeyEvent;

public abstract class Shortcut {

    protected CodeGalaxy codeGalaxy;

    public Shortcut(CodeGalaxy cg) {
        this.codeGalaxy = cg;
    }

    public void check(KeyEvent e) {
        if(validate(e)) {
            execute();
            e.consume();
        }
    }

    protected abstract boolean validate(KeyEvent e);

    protected abstract void execute();
}
