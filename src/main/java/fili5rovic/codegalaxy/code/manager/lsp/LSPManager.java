package fili5rovic.codegalaxy.code.manager.lsp;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.code.manager.highlighting.Range;
import fili5rovic.codegalaxy.lsp.LSP;
import fili5rovic.codegalaxy.util.SymbolUtil;
import javafx.application.Platform;
import org.eclipse.lsp4j.DocumentSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LSPManager extends Manager {

    public LSPManager(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    public void init() {
        LSP.instance().getDebouncer().addCompletionCallback(this::highlight);

        codeGalaxy.textProperty().addListener((_, _, _) -> onTextChanged());
    }

    private void onTextChanged() {
        LSP.instance().sendChangesDebounce(codeGalaxy.getFilePath().toString(), codeGalaxy.getText(), 200);
        Platform.runLater(this::highlight);
    }

    private void highlight() {
        CompletableFuture<List<DocumentSymbol>> symbols;
        try {
            symbols = LSP.instance().getAllSymbols(codeGalaxy.getFilePath().toString());
        } catch (Exception e) {
            System.err.println("Error getting symbols: " + e.getMessage());
            System.err.println("Couldn't highlight the code.");
            return;
        }

        HashMap<String, ArrayList<Range>> highlightRanges = SymbolUtil.getHighlightRanges(codeGalaxy, symbols);
        codeGalaxy.getHighlighter().setSymbolRanges(highlightRanges);
        codeGalaxy.getHighlighter().applyHighlighting();
    }
}
