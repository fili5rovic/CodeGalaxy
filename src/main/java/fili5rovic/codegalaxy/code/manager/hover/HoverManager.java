package fili5rovic.codegalaxy.code.manager.hover;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.lsp.LSP;
import fili5rovic.codegalaxy.util.Debouncer;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import org.eclipse.lsp4j.MarkedString;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.fxmisc.richtext.model.TwoDimensional;

import java.util.List;

public class HoverManager extends Manager {

    private final Tooltip hoverTooltip = new Tooltip();

    private final ScrollPane scrollPane = new ScrollPane();

    private final TextArea content = new TextArea();

    private final Debouncer hoverDebouncer = new Debouncer();

    private final int hoverDelay = 1000;

    private static final double MAX_WIDTH = 600;
    private static final double MAX_HEIGHT = 400;
    private static final double MIN_WIDTH = 200;
    private static final double MIN_HEIGHT = 50;

    public HoverManager(CodeGalaxy cg) {
        super(cg);
        hoverTooltip.setAutoHide(true);

        content.setEditable(false);

        scrollPane.setMaxSize(600, 400);
        scrollPane.setContent(content);
        hoverTooltip.setGraphic(scrollPane);
        hoverTooltip.setHideDelay(Duration.millis(300));

        hoverTooltip.setOnHidden(_ -> content.setText(""));
        content.setOnMouseExited(_ -> hoverTooltip.hide());
    }

    @Override
    public void init() {
        codeGalaxy.setOnMouseMoved(event -> {
            int characterIndex = offsetAt(codeGalaxy, event.getX(), event.getY());
            if (characterIndex != -1 && characterIndex < codeGalaxy.getLength()) {
                TwoDimensional.Position position = codeGalaxy.offsetToPosition(characterIndex, TwoDimensional.Bias.Forward);
                int line = position.getMajor();
                int column = position.getMinor();
                hoverDebouncer.debounce(() -> {
                    LSP.instance().hover(codeGalaxy.getFilePath().toString(), line, column).thenAccept(hoverInfo -> {
                        List<Either<String, MarkedString>> list = hoverInfo.getContents().getLeft();
                        if (list.isEmpty()) {
                            Platform.runLater(hoverTooltip::hide);
                            return;
                        }
                        StringBuilder tooltipText = new StringBuilder();
                        for (Either<String, MarkedString> item : list) {
                            String content = item.isLeft() ? item.getLeft() : item.getRight().getValue();
                            tooltipText.append(content).append('\n');
                        }
                        if (tooltipText.isEmpty()) {
                            Platform.runLater(hoverTooltip::hide);
                            return;
                        }
                        Platform.runLater(() -> {
                            if(codeGalaxy.getScene() == null || !codeGalaxy.getScene().getWindow().isFocused()) {
                                hoverTooltip.hide();
                                return;
                            }
                            content.setText(tooltipText.toString());
                            resizeToContent();
                            hoverTooltip.show(codeGalaxy, event.getScreenX() + 10, event.getScreenY() + 10);
                        });
                    });
                }, hoverDelay);
            } else {
                hoverTooltip.hide();
                hoverDebouncer.cancel();
            }
        });
    }

    private void resizeToContent() {
        String text = content.getText();
        if (text == null || text.isEmpty()) {
            return;
        }

        int lineCount = text.split("\n").length;

        int maxLineLength = 0;
        for (String line : text.split("\n")) {
            maxLineLength = Math.max(maxLineLength, line.length());
        }

        double charWidth = 7.5;
        double lineHeight = 18;

        double desiredWidth = Math.min(Math.max(maxLineLength * charWidth + 40, MIN_WIDTH), MAX_WIDTH);
        double desiredHeight = Math.min(Math.max(lineCount * lineHeight + 20, MIN_HEIGHT), MAX_HEIGHT);

        content.setPrefWidth(desiredWidth);
        content.setPrefHeight(desiredHeight);
        scrollPane.setPrefWidth(desiredWidth);
        scrollPane.setPrefHeight(desiredHeight);
    }

    private int offsetAt(CodeGalaxy codeArea, double x, double y) {
        return codeArea.hit(x, y).getCharacterIndex().orElse(-1);
    }

}
