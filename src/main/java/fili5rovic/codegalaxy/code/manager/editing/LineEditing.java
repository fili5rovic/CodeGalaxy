package fili5rovic.codegalaxy.code.manager.editing;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.code.manager.editing.shortcuts.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LineEditing extends Manager {

    public final Shortcut[] shortcuts = {
            new DeleteLineShortcut(codeGalaxy),
            new DuplicateLineBelow(codeGalaxy),
            new DuplicateLineAbove(codeGalaxy),
            new MoveLineUp(codeGalaxy),
            new MoveLineDown(codeGalaxy),
            new ExitShortcut(codeGalaxy),
            new WordSelect(codeGalaxy),
    };

    public final Shortcut[] tabShortcuts = new Shortcut[] {
            new IndentForward(codeGalaxy),
            new IndentBackward(codeGalaxy)
    };

    public LineEditing(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    public void init() {
        codeGalaxy.setOnKeyPressed(e -> {
            for (Shortcut s : shortcuts) {
                s.check(e);
            }
        });

        codeGalaxy.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                if(codeGalaxy.hasSelection())
                    event.consume();
                for(Shortcut tabS : tabShortcuts) {
                    tabS.check(event);
                }
            }
        });

    }
}
