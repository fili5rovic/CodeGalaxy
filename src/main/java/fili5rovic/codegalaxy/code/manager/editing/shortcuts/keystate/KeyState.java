package fili5rovic.codegalaxy.code.manager.editing.shortcuts.keystate;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyState {
    private final KeyCode code;
    private boolean controlDown;
    private boolean shiftDown;
    private boolean altDown;

    public KeyState(KeyCode code) {
        this.code = code;
        this.controlDown = false;
        this.shiftDown = false;
        this.altDown = false;
    }

    public KeyState ctrl() {
        this.controlDown = true;
        return this;
    }

    public KeyState shift() {
        this.shiftDown = true;
        return this;
    }

    public KeyState alt() {
        this.altDown = true;
        return this;
    }

    public boolean isActive(KeyEvent event) {
        return event.getCode().equals(code)
                && event.isControlDown() == controlDown
                && event.isShiftDown() == shiftDown
                && event.isAltDown() == altDown;
    }



}
