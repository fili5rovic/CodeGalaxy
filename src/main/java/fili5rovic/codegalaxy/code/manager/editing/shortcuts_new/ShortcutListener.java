package fili5rovic.codegalaxy.code.manager.editing.shortcuts_new;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class ShortcutListener {

    private final Set<KeyCode> targetCombination;
    private final Set<KeyCode> currentlyPressed = new HashSet<>();
    private final Runnable onShortcut;

    public ShortcutListener(Set<KeyCode> targetCombination, Runnable onShortcut) {
        this.targetCombination = targetCombination;
        this.onShortcut = onShortcut;
    }

    public void attachTo(javafx.scene.Node node) {
        node.addEventHandler(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
        node.addEventHandler(KeyEvent.KEY_RELEASED, this::handleKeyReleased);
    }

    public void attachTo(javafx.scene.Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
        scene.addEventHandler(KeyEvent.KEY_RELEASED, this::handleKeyReleased);
    }

    private void handleKeyPressed(KeyEvent event) {
        if (currentlyPressed.add(event.getCode())) {
            if (currentlyPressed.equals(targetCombination)) {
                onShortcut.run();
                // Optionally: currentlyPressed.clear(); // uncomment if you want to "consume" the shortcut
            }
        }
    }

    private void handleKeyReleased(KeyEvent event) {
        currentlyPressed.remove(event.getCode());
    }
}
