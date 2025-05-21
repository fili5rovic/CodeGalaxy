package fili5rovic.codegalaxy.code.manager.codeActions.rightClick;

import fili5rovic.codegalaxy.Main;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Popup;

import java.util.Objects;

public class TextFieldLabelPopup extends Popup {

    private final TextField textField;
    private final Label label;

    public TextFieldLabelPopup() {
        this.textField = new TextField();
        this.label = new Label("Rename");

        this.label.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/main-dark.css")).toExternalForm());

        getContent().add(label);
        getContent().add(textField);

        focusedProperty().addListener((observable, oldValue, isFocused) -> {
            if (!isFocused) {
                hide();
            }
        });
    }

    public TextField getTextField() {
        return textField;
    }

}
