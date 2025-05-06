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
            if (e.getCode().isArrowKey())
                return;

            if (e.getCode() == KeyCode.BACK_SPACE) {
                if (console.getCaretColumn() == 0) {
                    e.consume();
                    return;
                }
                if (input.length() > 0) {
                    input.deleteCharAt(input.length() - 1);
                }
                return;
            }

            if (console.getCaretPosition() < console.getLength()) {
                e.consume();
                console.appendText(e.getText());
            }

            input.append(e.getText());
        });
    }

}

