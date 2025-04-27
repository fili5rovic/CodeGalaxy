package fili5rovic.codegalaxy.code.manager.editing.shortcuts.keystate;

import fili5rovic.codegalaxy.settings.ProjectSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyState {
    private KeyCode code;
    private boolean controlDown;
    private boolean shiftDown;
    private boolean altDown;

    private final String shortcutName;

    public KeyState(KeyCode code) {
        this.code = code;
        this.controlDown = false;
        this.shiftDown = false;
        this.altDown = false;
        this.shortcutName = null;
    }

    public KeyState(String savedKeyName) {
        this.shortcutName = savedKeyName;
        String savedCode = ProjectSettings.getInstance().get("shortcut_" + savedKeyName);

        String[] parts = savedCode.split("\\|");
        this.code = KeyCode.valueOf(parts[0]);
        this.controlDown = parts[1].equals("1");
        this.shiftDown = parts[2].equals("1");
        this.altDown = parts[3].equals("1");
    }

    public String getShortcutName() {
        return shortcutName;
    }

    public KeyCode getCode() {
        return code;
    }

    public boolean isControlDown() {
        return controlDown;
    }

    public boolean isShiftDown() {
        return shiftDown;
    }

    public boolean isAltDown() {
        return altDown;
    }

    public void setCode(KeyCode code) {
        this.code = code;
    }

    public void setControlDown(boolean controlDown) {
        this.controlDown = controlDown;
    }
    public void setShiftDown(boolean shiftDown) {
        this.shiftDown = shiftDown;
    }
    public void setAltDown(boolean altDown) {
        this.altDown = altDown;
    }


    public KeyState shift() {
        this.shiftDown = true;
        return this;
    }


    public boolean isActive(KeyEvent event) {
        return event.getCode().equals(code)
                && event.isControlDown() == controlDown
                && event.isShiftDown() == shiftDown
                && event.isAltDown() == altDown;
    }

    @Override
    public String toString() {
        return code.toString() + "|" +
                (controlDown ? "1" : "0") + "|" +
                (shiftDown ? "1" : "0") + "|" +
                (altDown ? "1" : "0");
    }

    public static void main(String[] args) {
        System.out.println(KeyCode.UP);
    }
}
