package fili5rovic.codegalaxy.code.manager.hover;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.lsp.LSP;
import fili5rovic.codegalaxy.util.Debouncer;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.MarkedString;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.fxmisc.richtext.model.TwoDimensional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HoverManager extends Manager {

    private final Tooltip hoverTooltip = new Tooltip();

    private final TextArea content = new TextArea();

    private final Debouncer hoverDebouncer = new Debouncer();

    private final int HOVER_DELAY = 300;

    private String lastHoveredWord = null;

    private static final double MAX_WIDTH = 600;
    private static final double MAX_HEIGHT = 400;
    private static final double MIN_WIDTH = 200;
    private static final double MIN_HEIGHT = 50;

    private record WordInfo(String word, int startIndex) {
    }

    public HoverManager(CodeGalaxy cg) {
        super(cg);

        content.setEditable(false);

        hoverTooltip.setGraphic(content);

        hoverTooltip.setOnHidden(_ -> content.setText(""));
    }

    @Override
    public void init() {
        codeGalaxy.setOnMouseMoved(this::handleMouseMoved);
    }

    private void handleMouseMoved(MouseEvent event) {
        if (!hoverTooltip.isShowing()) {
            hoverDebouncer.debounce(() -> Platform.runLater(() -> handleEvent(event)), HOVER_DELAY);
        } else {
            handleEvent(event);
            hoverDebouncer.cancel();
        }
    }


    private void handleEvent(MouseEvent event) {

        int characterIndex = offsetAt(codeGalaxy, event.getX(), event.getY());
        if (characterIndex == -1 || characterIndex >= codeGalaxy.getLength()) {
            hideTooltip();
            lastHoveredWord = null;
            return;
        }

        TwoDimensional.Position position = codeGalaxy.offsetToPosition(characterIndex, TwoDimensional.Bias.Forward);

        int line = position.getMajor();
        int column = position.getMinor();
        String text = codeGalaxy.getParagraph(line).getText();
        WordInfo wordInfo = extractWordAt(text, column);
        if (wordInfo == null) {
            lastHoveredWord = null;
            hideTooltip();
            hoverDebouncer.cancel();
            return;
        }

        String currentWord = wordInfo.word;

        if (lastHoveredWord == null) {
            if (currentWord.isEmpty()) {
                hideTooltip();
                return;
            }
            int startOffset = codeGalaxy.position(line, wordInfo.startIndex).toOffset();
            Optional<Bounds> bounds = codeGalaxy.getCharacterBoundsOnScreen(startOffset, startOffset + 1);

            bounds.ifPresent(b -> LSP.instance().hover(codeGalaxy.getFilePath().toString(), line, column)
                    .thenAccept(hoverInfo -> processHoverInfo(hoverInfo, b.getMinX(), b.getMaxY())));

        } else if (lastHoveredWord.equals(currentWord)) {
            return;
        } else {
            hideTooltip();
            currentWord = null;
        }

        lastHoveredWord = currentWord;
    }

    private WordInfo extractWordAt(String lineText, int column) {
        if (column < 0 || column >= lineText.length()) return null;

        int start = column;
        int end = column;

        while (start > 0 && Character.isJavaIdentifierPart(lineText.charAt(start - 1))) {
            start--;
        }
        while (end < lineText.length() && Character.isJavaIdentifierPart(lineText.charAt(end))) {
            end++;
        }

        if (start == end) return null;

        return new WordInfo(lineText.substring(start, end), start);
    }


    private void processHoverInfo(Hover hoverInfo, double screenX, double screenY) {
        List<Either<String, MarkedString>> contents = hoverInfo.getContents().getLeft();


        if (contents.isEmpty()) {
            Platform.runLater(hoverTooltip::hide);
            return;
        }

        String tooltipText = contents.stream()
                .map(item -> item.isLeft() ? item.getLeft() : item.getRight().getValue())
                .collect(Collectors.joining("\n"));

        if(tooltipText.isBlank())
            return;

        Platform.runLater(() -> showTooltip(tooltipText, screenX, screenY));
    }

    private void showTooltip(String text, double screenX, double screenY) {
        if (!isWindowFocused()) {
            hoverTooltip.hide();
            return;
        }

        content.setText(text);
        resizeToContent();
        hoverTooltip.show(codeGalaxy, screenX, screenY);
    }

    private boolean isWindowFocused() {
        return codeGalaxy.getScene() != null && codeGalaxy.getScene().getWindow().isFocused();
    }

    private void hideTooltip() {
        hoverTooltip.hide();
        hoverDebouncer.cancel();
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
        content.setPrefHeight(desiredHeight + 10);
    }

    private int offsetAt(CodeGalaxy codeArea, double x, double y) {
        return codeArea.hit(x, y).getCharacterIndex().orElse(-1);
    }

}
