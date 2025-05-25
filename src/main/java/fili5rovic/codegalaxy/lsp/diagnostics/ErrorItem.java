package fili5rovic.codegalaxy.lsp.diagnostics;

import fili5rovic.codegalaxy.util.SVGUtil;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;

public class ErrorItem extends Label {

    private final Range range;

    public ErrorItem(Diagnostic diagnostic) {
        super(diagnostic.getMessage() + " (" + diagnostic.getRange().getStart().getLine() + ":" + diagnostic.getRange().getStart().getCharacter() + ")");
        setupIcon(diagnostic);

        this.range = diagnostic.getRange();

        setPadding(new javafx.geometry.Insets(0, 0, 0, 20));

        setFont(new Font(16));
    }

    private void setupIcon(Diagnostic diagnostic) {
        String type = diagnostic.getSeverity().toString().toLowerCase();
        setGraphic(SVGUtil.getIcon(type, 16, 16));
    }

    public Range getRange() {
        return range;
    }
}
