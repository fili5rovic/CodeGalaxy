package fili5rovic.codegalaxy.code.manager.lsp;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.code.manager.highlighting.Range;
import fili5rovic.codegalaxy.lsp.LSPManager;
import fili5rovic.codegalaxy.util.SymbolUtil;
import org.eclipse.lsp4j.DocumentSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LSPHelper extends Manager {

    public LSPHelper(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    public void init() {
        codeGalaxy.textProperty().addListener((_, _, _) -> {
            onTextChanged();
        });
    }

    private void onTextChanged() {
        LSPManager.getInstance().sendChangesDebounce(codeGalaxy.getFilePath().toString(), codeGalaxy.getText(), 400);

        CompletableFuture<List<DocumentSymbol>> symbols;
        try {
            symbols = LSPManager.getInstance().getAllSymbols(codeGalaxy.getFilePath().toString());
        } catch (Exception e) {
            System.err.println("Error getting symbols: " + e.getMessage());
            System.err.println("Couldn't highlight the code.");
            return;
        }

        HashMap<String, ArrayList<Range>> highlightRanges = SymbolUtil.getHighlightRanges(codeGalaxy, symbols);
        codeGalaxy.getHighlighter().setSymbolRanges(highlightRanges);
        codeGalaxy.getHighlighter().applyHighlighting(codeGalaxy);
    }
}
