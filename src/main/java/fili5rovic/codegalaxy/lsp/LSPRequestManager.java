package fili5rovic.codegalaxy.lsp;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageServer;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LSPRequestManager {

    private final LanguageServer server;
    private final LSPDocumentManager documentManager;

    public LSPRequestManager(LanguageServer server, LSPDocumentManager documentManager) {
        this.server = server;
        this.documentManager = documentManager;
    }

    public List<CompletionItem> requestCompletions(String filePath, int line, int character) throws Exception {
        String uri = Paths.get(filePath).toUri().toString();

        String text = documentManager.getDocumentContents().get(uri);
        if (text == null) {
            throw new IllegalStateException("No content found for " + uri + ". Check if file is opened");
        }
        
        TextDocumentIdentifier docId = new TextDocumentIdentifier(uri);
        Position pos = new Position(line, character);
        CompletionParams params = new CompletionParams(docId, pos);

        CompletableFuture<Either<List<CompletionItem>, CompletionList>> future =
                server.getTextDocumentService().completion(params);

        Either<List<CompletionItem>, CompletionList> result = future.get();

        return result.isLeft()
                ? result.getLeft()
                : result.getRight().getItems();
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

    public CompletableFuture<List<? extends TextEdit>> formatDocument(String filePath) {
        String uri = Paths.get(filePath).toUri().toString();
        TextDocumentIdentifier docId = new TextDocumentIdentifier(uri);

        FormattingOptions options = new FormattingOptions(4, true);
        DocumentFormattingParams params = new DocumentFormattingParams(docId, options);

        return server.getTextDocumentService()
                .formatting(params);
    }

    public CompletableFuture<List<? extends Location>> goToDefinition(String filePath, int line, int character) {
        String uri = Paths.get(filePath).toUri().toString();
        TextDocumentIdentifier docId = new TextDocumentIdentifier(uri);
        Position pos = new Position(line, character);
        DefinitionParams params = new DefinitionParams(docId, pos);

        return server.getTextDocumentService()
                .definition(params)
                .thenApply(either -> {
                    if (either == null) return List.of();
                    if (either.isLeft())
                        return either.getLeft();
                    // for LocationLink, it's not supported by the current LSP version
                    System.err.println("Unexpected response type for goToDefinition: " + either.getRight());
                    return List.of();

                });
    }

    public CompletableFuture<List<? extends Location>> references(String filePath, int line, int character) {
        String uri = Paths.get(filePath).toUri().toString();
        TextDocumentIdentifier docId = new TextDocumentIdentifier(uri);
        Position pos = new Position(line, character);
        ReferenceContext context = new ReferenceContext(false);

        ReferenceParams params = new ReferenceParams(docId, pos, context);

        return server.getTextDocumentService().references(params).thenApply(e -> {
            if (e == null || e.isEmpty()) {
                System.out.println("No references found.");
                return List.of();
            }
            return e;
        });
    }

    public CompletableFuture<Hover> hover(String filePath, int line, int character) {
        String uri = Paths.get(filePath).toUri().toString();
        TextDocumentIdentifier docId = new TextDocumentIdentifier(uri);
        Position pos = new Position(line, character);

        HoverParams params = new HoverParams(docId, pos);

        return server.getTextDocumentService().hover(params);
    }


}
