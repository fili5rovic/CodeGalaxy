package fili5rovic.codegalaxy.code.manager.hover;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.lsp.LSP;
import fili5rovic.codegalaxy.util.Debouncer;
import javafx.application.Platform;
import javafx.scene.control.Tooltip;
import org.eclipse.lsp4j.MarkedString;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.fxmisc.richtext.model.TwoDimensional;

import java.util.List;

public class HoverManager extends Manager {

    private final Tooltip hoverTooltip = new Tooltip();

    private final Debouncer hoverDebouncer = new Debouncer();

    private final int hoverDelay = 1000;

    public HoverManager(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    public void init() {
        codeGalaxy.setOnMouseMoved(event -> {
            hoverTooltip.hide();
            int characterIndex = offsetAt(codeGalaxy, event.getX(), event.getY());
            if (characterIndex != -1 && characterIndex < codeGalaxy.getLength()) {
                TwoDimensional.Position position = codeGalaxy.offsetToPosition(characterIndex, TwoDimensional.Bias.Forward);
                int line = position.getMajor();
                int column = position.getMinor();
                hoverDebouncer.debounce(() -> {
                    LSP.instance().hover(codeGalaxy.getFilePath().toString(), line, column).thenAccept(hoverInfo -> {
                        System.out.println(hoverInfo);
                        List<Either<String, MarkedString>> list = hoverInfo.getContents().getLeft();
                        if (list.isEmpty()) {
                            hoverTooltip.hide();
                            return;
                        }
                        StringBuilder tooltipText = new StringBuilder();
                        for (Either<String, MarkedString> item : list) {
                            String content = item.isLeft() ? item.getLeft() : item.getRight().getValue();
                            tooltipText.append(content);
                        }
                        hoverTooltip.setText(tooltipText.toString());
                        Platform.runLater(() -> hoverTooltip.show(codeGalaxy, event.getScreenX() + 10, event.getScreenY() + 10));
                    });
                }, hoverDelay);
            } else {
                hoverTooltip.hide();
                hoverDebouncer.cancel();
            }
        });
    }

    private int offsetAt(CodeGalaxy codeArea, double x, double y) {
        return codeArea.hit(x, y).getCharacterIndex().orElse(-1);
    }

}
