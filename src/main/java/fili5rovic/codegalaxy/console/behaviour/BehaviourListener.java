package fili5rovic.codegalaxy.console.behaviour;

import fili5rovic.codegalaxy.console.ConsoleArea;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class BehaviourListener {

    public static void apply(ConsoleArea console) {
        console.setEditable(true);
        console.setWrapText(true);

        StringBuilder input = new StringBuilder();

        console.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode().isArrowKey()) {
                e.consume();
                return;
            }

            if (e.getCode() == KeyCode.ENTER) {
                console.appendText("\n");
                if (!input.isEmpty()) {
                    console.writeInput(input.toString());
                    input.setLength(0);
                }
                e.consume();
            } else if (e.getCode() == KeyCode.BACK_SPACE) {
                if (!input.isEmpty()) {
                    input.deleteCharAt(input.length() - 1);
                    int caret = console.getCaretPosition();
                    if (caret > 0) {
                        console.replaceText(caret - 1, caret, "");
                    }
                }
                e.consume();
            }
        });

        console.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            String character = e.getCharacter();
            if (character.isEmpty() || character.equals("\r") || character.equals("\n"))
                return;

            input.append(character);
            console.appendText(character);
            e.consume();
        });
    }


}

