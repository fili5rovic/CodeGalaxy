package fili5rovic.codegalaxy.lsp;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageServer;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LSPRequestManager {

    private LanguageServer server;
    private LSPDocumentManager documentManager;

    public LSPRequestManager(LanguageServer server, LSPDocumentManager documentManager) {
        this.server = server;
        this.documentManager = documentManager;
    }

    public List<CompletionItem> requestCompletions(String filePath, int line, int character) throws Exception {
        String uri = Paths.get(filePath).toUri().toString();

        String text = documentManager.getDocumentContents().get(uri);
        if (text == null) {
            throw new IllegalStateException("No content found for " + uri + ". Did you open the file?");
        }
        String[] lines = text.split("\n");
        String lineText = lines[line];
        System.out.println("Line " + line + ": " + lineText + " (length: " + lineText.length() + ")");
        if (character < lineText.length()) {
            System.out.println("Character " + character + ": " + lineText.charAt(character));
        } else {
            System.out.println("At end of line " + line + ", position " + character);
        }

        TextDocumentIdentifier docId = new TextDocumentIdentifier(uri);
        Position pos = new Position(line, character);
        CompletionParams params = new CompletionParams(docId, pos);

        CompletableFuture<Either<List<CompletionItem>, CompletionList>> future =
                server.getTextDocumentService().completion(params);

        Either<List<CompletionItem>, CompletionList> result = future.get();
        List<CompletionItem> items = result.isLeft()
                ? result.getLeft()
                : result.getRight().getItems();

        return items;
    }

    public CompletableFuture<List<DocumentSymbol>> getAllSymbols(String filePath) {
        return requestDocumentSymbols(filePath)
                .thenApply(eithers -> {
                    List<DocumentSymbol> flat = new ArrayList<>();
                    for (Either<SymbolInformation, DocumentSymbol> e : eithers) {
                        if (e.isRight()) collect(e.getRight(), flat);
                    }
                    return flat;
                });
    }

    private CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> requestDocumentSymbols(String filePath) {
        String uri = Paths.get(filePath).toUri().toString();
        DocumentSymbolParams params = new DocumentSymbolParams(new TextDocumentIdentifier(uri));
        return server.getTextDocumentService()
                .documentSymbol(params);
    }


    private void collect(DocumentSymbol ds, List<DocumentSymbol> out) {
        out.add(ds);
        if (ds.getChildren() != null) {
            for (DocumentSymbol c : ds.getChildren()) collect(c, out);
        }
    }
}
