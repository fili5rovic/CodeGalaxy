package fili5rovic.codegalaxy.code.manager.editing.shortcuts_new;

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
        registerFromSettings();

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

    private void registerFromSettings() {
        registerShortcut("shortcut_word_select", () -> codeGalaxy.selectWord());
        registerShortcut("shortcut_delete_line", () -> ShortcutActions.deleteLine(codeGalaxy));
        registerShortcut("shortcut_move_line_up", () -> ShortcutActions.moveLineUp(codeGalaxy));
        registerShortcut("shortcut_move_line_down", () -> ShortcutActions.moveLineDown(codeGalaxy));
        registerShortcut("shortcut_comment_line", () -> ShortcutActions.commentLine(codeGalaxy));
        registerShortcut("shortcut_duplicate_line_above", () -> ShortcutActions.duplicateLineAbove(codeGalaxy));
        registerShortcut("shortcut_duplicate_line_below", () -> ShortcutActions.duplicateLineBelow(codeGalaxy));

    }

    public void registerShortcut(String id, Runnable callback) {
        callbacks.put(id, callback);

        String shortcutString = IDESettings.getInstance().get(id);
        if (shortcutString != null && !shortcutString.isEmpty()) {
            try {
                shortcuts.put(id, parse(shortcutString));
            } catch (IllegalArgumentException ex) {
                System.err.println("Invalid shortcut for " + id + ": " + shortcutString);
            }
        }
    }

    private static KeyCodeCombination parse(String input) {
        String[] tokens = input.toUpperCase().split("\\+");
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
                case "META":
                    modifiers.add(KeyCombination.META_DOWN);
                    break;
                default:
                    try {
                        mainKey = KeyCode.valueOf(token);
                    } catch (IllegalArgumentException ex) {
                        throw new IllegalArgumentException("Unknown key: " + token);
                    }
            }
        }

        if (mainKey == null)
            throw new IllegalArgumentException("No main key in: " + input);

        return new KeyCodeCombination(mainKey, modifiers.toArray(new KeyCombination.Modifier[0]));
    }
}
