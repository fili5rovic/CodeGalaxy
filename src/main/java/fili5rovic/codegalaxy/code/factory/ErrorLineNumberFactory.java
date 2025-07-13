package fili5rovic.codegalaxy.code.factory;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.lsp.diagnostics.DiagnosticsListener;
import fili5rovic.codegalaxy.lsp.diagnostics.DiagnosticsPublisher;
import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.util.SVGUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.PublishDiagnosticsParams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public class ErrorLineNumberFactory implements IntFunction<Node>, DiagnosticsListener {

    private static final int BASE_WIDTH = 60;
    private static final int PADDING_LEFT = 5;
    private static final int PADDING_RIGHT = 5;
    private static final int INDICATOR_SPACE_ADDON = 8;
    private static final int MIN_INDICATOR_SPACE = 20;

    private static final int MIN_INDICATOR_SIZE = 12;
    private static final int INDICATOR_SIZE_DIVIDER = 2;
    private static final int ICON_SIZE_REDUCTION = 0;
    private static final int MIN_ICON_SIZE = 8;

    private static final int FALLBACK_FONT_DIVIDER = 3;
    private static final int MIN_FALLBACK_FONT = 6;

    private static final int TOOLTIP_DELAY_MS = 300;

    private final CodeGalaxy codeGalaxy;
    private final Map<Integer, List<Diagnostic>> errorsByLine = new HashMap<>();
    private int fontSize;

    public ErrorLineNumberFactory(CodeGalaxy codeGalaxy) {
        this.codeGalaxy = codeGalaxy;
        this.fontSize = Integer.parseInt(IDESettings.getInstance().get("fontSize"));
        DiagnosticsPublisher.instance().subscribe(this);
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    @Override
    public Node apply(int lineNumber) {
        HBox lineBox = new HBox();
        lineBox.setAlignment(Pos.CENTER_RIGHT);
        lineBox.setPadding(new Insets(0, PADDING_RIGHT, 0, PADDING_LEFT));

        int indicatorSpace = Math.max(MIN_INDICATOR_SPACE, fontSize / INDICATOR_SIZE_DIVIDER + INDICATOR_SPACE_ADDON);
        int totalWidth = BASE_WIDTH + indicatorSpace;

        lineBox.setMinWidth(totalWidth);
        lineBox.setPrefWidth(totalWidth);
        lineBox.setMaxWidth(totalWidth);

        lineBox.getStyleClass().add("line-number-box");

        Label lineText = new Label(String.valueOf(lineNumber + 1));
        lineText.getStyleClass().add("line-number-text");

        Node errorIndicator = createErrorIndicator(lineNumber);

        lineBox.getChildren().add(lineText);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        lineBox.getChildren().add(spacer);

        if (errorIndicator != null) {
            lineBox.getChildren().add(errorIndicator);
        } else {
            // Add invisible placeholder to maintain consistent spacing
            Region placeholder = new Region();
            int indicatorSize = Math.max(MIN_INDICATOR_SIZE, fontSize / INDICATOR_SIZE_DIVIDER);
            placeholder.setMinSize(indicatorSize, indicatorSize);
            placeholder.setMaxSize(indicatorSize, indicatorSize);
            lineBox.getChildren().add(placeholder);
        }

        return lineBox;
    }

    private Node createErrorIndicator(int lineNumber) {
        List<Diagnostic> diagnostics = errorsByLine.get(lineNumber);
        if (diagnostics == null || diagnostics.isEmpty()) {
            return null;
        }

        // Find the highest severity diagnostic for this line
        Diagnostic mostSevere = diagnostics.stream()
                .min((d1, d2) -> {
                    // Error = 1, Warning = 2, Info = 3, Hint = 4
                    int severity1 = d1.getSeverity() != null ? d1.getSeverity().getValue() : 4;
                    int severity2 = d2.getSeverity() != null ? d2.getSeverity().getValue() : 4;
                    return Integer.compare(severity1, severity2);
                })
                .orElse(diagnostics.getFirst());

        // Create indicator based on severity
        int indicatorSize = Math.max(MIN_INDICATOR_SIZE, fontSize / INDICATOR_SIZE_DIVIDER);
        Label indicator = new Label();
        indicator.setMinSize(indicatorSize, indicatorSize);
        indicator.setMaxSize(indicatorSize, indicatorSize);

        String iconType;
        String tooltipText = createTooltipText(diagnostics);

        if (mostSevere.getSeverity() == DiagnosticSeverity.Error) {
            iconType = "error";
            indicator.getStyleClass().add("error-indicator");
        } else if (mostSevere.getSeverity() == DiagnosticSeverity.Warning) {
            iconType = "warning";
            indicator.getStyleClass().add("warning-indicator");
        } else {
            iconType = "info";
            indicator.getStyleClass().add("info-indicator");
        }

        int iconSize = Math.max(MIN_ICON_SIZE, indicatorSize - ICON_SIZE_REDUCTION);
        indicator.setGraphic(SVGUtil.getIcon(iconType, iconSize, iconSize));

        if (tooltipText != null && !tooltipText.isEmpty()) {
            Tooltip tooltip = new Tooltip(tooltipText);
            tooltip.setShowDelay(javafx.util.Duration.millis(TOOLTIP_DELAY_MS));
            Tooltip.install(indicator, tooltip);
        }

        return indicator;
    }

    private String createTooltipText(List<Diagnostic> diagnostics) {
        return diagnostics.stream()
                .map(d -> {
                    String severity = d.getSeverity() != null ?
                            d.getSeverity().toString().toLowerCase() : "info";
                    return "[" + severity.toUpperCase() + "] " + d.getMessage();
                })
                .collect(Collectors.joining("\n"));
    }

    @Override
    public void onDiagnosticsUpdated(String uri, PublishDiagnosticsParams params) {
        // Check if this update is for the current file
        if (codeGalaxy.getFilePath() == null ||
                !uri.equals(codeGalaxy.getFilePath().toUri().toString())) {
            return;
        }

        Platform.runLater(() -> {
            // Clear existing errors
            errorsByLine.clear();

            // Process new diagnostics
            if (params != null && params.getDiagnostics() != null) {
                for (Diagnostic diagnostic : params.getDiagnostics()) {
                    int lineNumber = diagnostic.getRange().getStart().getLine();
                    errorsByLine.computeIfAbsent(lineNumber, _ -> new java.util.ArrayList<>())
                            .add(diagnostic);
                }
            }

            // Refresh the line number display
            refreshLineNumbers();
        });
    }

    private void refreshLineNumbers() {
        // Force CodeArea to refresh its paragraph graphics
        codeGalaxy.setParagraphGraphicFactory(null);
        codeGalaxy.setParagraphGraphicFactory(this);
    }

    public void dispose() {
        DiagnosticsPublisher.instance().unsubscribe(this);
    }
}