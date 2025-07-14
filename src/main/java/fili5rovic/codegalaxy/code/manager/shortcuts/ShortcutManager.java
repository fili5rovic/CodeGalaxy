package fili5rovic.codegalaxy.code.manager.shortcuts;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.settings.IDESettings;
import javafx.scene.input.*;

import java.util.*;

public class ShortcutManager extends Manager {

    private final Map<String, KeyCodeCombination> shortcuts = new HashMap<>();
    private final Map<String, Runnable> callbacks = new HashMap<>();

    public ShortcutManager(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    public void init() {
        // 1. Register all possible actions and their default callbacks
        registerAllActions();

        // 2. Load the key combinations from user settings
        loadKeyCombinations();

        // 3. Add a single event filter to listen for all shortcuts
        codeGalaxy.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            for (Map.Entry<String, KeyCodeCombination> entry : shortcuts.entrySet()) {
                String id = entry.getKey();
                KeyCodeCombination combo = entry.getValue();

                if (combo != null && combo.match(e)) {
                    Runnable callback = callbacks.get(id);
                    if (callback != null) {
                        callback.run();
                        e.consume();
                    }
                }
            }
        });
    }

    /**
     * Clears and reloads all shortcut key combinations from the settings.
     * This method should be called after shortcut settings have been changed.
     */
    public void reloadShortcuts() {
        shortcuts.clear();
        loadKeyCombinations();
        System.out.println("Shortcuts have been reloaded."); // For verification
    }

    /**
     * Populates the callbacks map with all known shortcut IDs and their corresponding actions.
     * This only needs to be done once.
     */
    private void registerAllActions() {
        callbacks.put("shortcut_word_select", () -> codeGalaxy.selectWord());
        callbacks.put("shortcut_delete_line", () -> ShortcutActions.deleteLine(codeGalaxy));
        callbacks.put("shortcut_move_line_up", () -> ShortcutActions.moveLineUp(codeGalaxy));
        callbacks.put("shortcut_move_line_down", () -> ShortcutActions.moveLineDown(codeGalaxy));
        callbacks.put("shortcut_comment_line", () -> ShortcutActions.commentLine(codeGalaxy));
        callbacks.put("shortcut_duplicate_line_above", () -> ShortcutActions.duplicateLineAbove(codeGalaxy));
        callbacks.put("shortcut_duplicate_line_below", () -> ShortcutActions.duplicateLineBelow(codeGalaxy));
    }

    /**
     * Reads the shortcut strings from IDESettings and populates the `shortcuts` map.
     */
    private void loadKeyCombinations() {
        IDESettings settings = IDESettings.getInstance();
        for (String id : callbacks.keySet()) {
            String shortcutString = settings.get(id);
            if (shortcutString != null && !shortcutString.isEmpty()) {
                try {
                    shortcuts.put(id, parse(shortcutString));
                } catch (IllegalArgumentException ex) {
                    System.err.println("Invalid shortcut for " + id + ": " + shortcutString);
                }
            }
        }
    }

    private static KeyCodeCombination parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty.");
        }
        String[] tokens = input.toUpperCase().split("\\s*\\+\\s*");
        KeyCode mainKey = null;
        List<KeyCombination.Modifier> modifiers = new ArrayList<>();

        for (String token : tokens) {
            switch (token.trim()) {
                case "CTRL":
                case "CONTROL":
                    modifiers.add(KeyCombination.CONTROL_DOWN);
                    break;
                case "SHIFT":
                    modifiers.add(KeyCombination.SHIFT_DOWN);
                    break;
                case "ALT":
                    modifiers.add(KeyCombination.ALT_DOWN);
                    break;
                case "META": // For MacOS (Command key) or Windows key
                    modifiers.add(KeyCombination.META_DOWN);
                    break;
                default:
                    if (mainKey != null) {
                        throw new IllegalArgumentException("Multiple main keys defined in: " + input);
                    }
                    try {
                        mainKey = KeyCode.valueOf(token);
                    } catch (IllegalArgumentException ex) {
                        throw new IllegalArgumentException("Unknown key: " + token);
                    }
            }
        }

        if (mainKey == null) {
            throw new IllegalArgumentException("No main key found in: " + input);
        }

        return new KeyCodeCombination(mainKey, modifiers.toArray(new KeyCombination.Modifier[0]));
    }
}