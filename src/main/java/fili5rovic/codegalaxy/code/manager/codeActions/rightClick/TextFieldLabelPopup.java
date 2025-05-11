package fili5rovic.codegalaxy.code.manager.codeActions.rightClick;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Popup;

public class TextFieldLabelPopup extends Popup {

    private final TextField textField;
    private final Label label;

    public TextFieldLabelPopup() {
        this.textField = new TextField();
        this.label = new Label("Rename");

        getContent().add(label);
        getContent().add(textField);
    }

    public TextField getTextField() {
        return textField;
    }

}
