package fili5rovic.codegalaxy.code.manager.editing.shortcuts.keystate;

import fili5rovic.codegalaxy.settings.ProjectSettings;
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

    public KeyState(String savedKeyName) {
        String savedCode = ProjectSettings.getInstance().get("shortcut_" + savedKeyName);

        String[] parts = savedCode.split("\\|");
        this.code = KeyCode.valueOf(parts[0]);
        this.controlDown = parts[1].equals("1");
        this.shiftDown = parts[2].equals("1");
        this.altDown = parts[3].equals("1");
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
