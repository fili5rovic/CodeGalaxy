package fili5rovic.codegalaxy.lsp.diagnostics;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.controller.Controllers;
import fili5rovic.codegalaxy.util.SVGUtil;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;

public class ErrorItem extends HBox {

    private final Range range;

    public ErrorItem(Diagnostic diagnostic) {
        this.range = diagnostic.getRange();

        structure(diagnostic);

        listeners();

        setPadding(new Insets(0, 0, 0, 20));
    }

    private void listeners() {
        setCursor(Cursor.HAND);

        int line = range.getStart().getLine();
        int column = range.getStart().getCharacter();

        setOnMouseClicked(_ -> {
            CodeGalaxy openCodeGalaxy = Controllers.dashboardController().getCurrentOpenCodeGalaxy();
            if(openCodeGalaxy == null)
                return;

            openCodeGalaxy.requestFocus();
            openCodeGalaxy.moveTo(line, column);
            openCodeGalaxy.requestFollowCaret();
        });

    }

    private void structure(Diagnostic diagnostic) {
        Label label = new Label(diagnostic.getMessage());
        label.setFont(new Font(16));

        String type = diagnostic.getSeverity().toString().toLowerCase();
        label.setGraphic(SVGUtil.getIcon(type, 16));

        int line = range.getStart().getLine() + 1;
        Label lineNumberLabel = new Label(" :" + line);
        lineNumberLabel.getStyleClass().add("label-secondary");
        lineNumberLabel.setFont(new Font(16));

        getChildren().addAll(label, lineNumberLabel);
    }
}
