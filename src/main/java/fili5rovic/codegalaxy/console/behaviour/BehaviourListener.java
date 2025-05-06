package fili5rovic.codegalaxy.console.behaviour;

import fili5rovic.codegalaxy.console.ConsoleArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class BehaviourListener {

    public static void apply(ConsoleArea console) {
        console.setEditable(true);
        console.setWrapText(true);

        StringBuilder input = new StringBuilder();

        console.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER) {
                console.appendText("\n");
                console.setTextType(ConsoleArea.OUTPUT);
                if (!input.isEmpty()) {
                    console.getRedirector().sendInput(input.toString());
                    input.setLength(0);
                }
                e.consume();
            } else if (e.getCode() == KeyCode.BACK_SPACE) {
                if (input.length() > 0) {
                    input.setLength(input.length() - 1);

                    int caretPosition = console.getCaretPosition();

                    if (caretPosition > 0) {
                        console.deleteText(caretPosition - 1, caretPosition);
                    }
                }
                e.consume();
            }
        });

        console.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            if (e.isControlDown()) {
                return;
            }

            String character = e.getCharacter();
            if (character.isEmpty() || character.equals("\r") || character.equals("\n") ||
                    character.equals("\b")) {
                e.consume();
                return;
            }

            console.setTextType(ConsoleArea.INPUT);
            input.append(character);
            console.appendText(character);
            e.consume();
        });
    }


}

