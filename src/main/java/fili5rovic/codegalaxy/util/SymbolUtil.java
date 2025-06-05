package fili5rovic.codegalaxy.util;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.highlighting.Range;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.SymbolKind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SymbolUtil {


    public static HashMap<String, ArrayList<Range>> getHighlightRanges(CodeGalaxy codeGalaxy, CompletableFuture<List<DocumentSymbol>> symbols) {
        HashMap<String, ArrayList<Range>> symbolRanges = new HashMap<>();

        try {
            List<DocumentSymbol> documentSymbols = symbols.get();

            for (DocumentSymbol symbol : documentSymbols) {
                Range range = new Range(codeGalaxy.position(symbol.getSelectionRange().getStart().getLine(), symbol.getSelectionRange().getStart().getCharacter()).toOffset(), codeGalaxy.position(symbol.getSelectionRange().getEnd().getLine(), symbol.getSelectionRange().getEnd().getCharacter()).toOffset());

                if (symbol.getKind() == SymbolKind.Package) {
                    range = new Range(range.start() + 8, range.end() - 1);
                }
                symbolRanges.computeIfAbsent(symbol.getKind().toString().toLowerCase(), _ -> new ArrayList<>()).add(range);
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while getting symbol ranges: " + e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            System.err.println("IndexOutOfBounds: " + e.getMessage());
        }

        return symbolRanges;
    }

}
