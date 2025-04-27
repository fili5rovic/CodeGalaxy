package fili5rovic.codegalaxy.code.manager.editing.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.editing.shortcuts.keystate.KeyState;
import javafx.scene.input.KeyEvent;

public abstract class Shortcut {

    protected CodeGalaxy codeGalaxy;
    protected String shortcutName;

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

    protected final boolean validate(KeyEvent e) {
        return getKeyState().isActive(e);
    }

    protected void executeSingle() {
        executeSelection();
    }

    protected KeyState getKeyState() {
        return new KeyState(shortcutName);
    }

    protected abstract void executeSelection();

}
