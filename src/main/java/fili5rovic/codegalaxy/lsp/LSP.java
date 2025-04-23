package fili5rovic.codegalaxy.lsp;

import fili5rovic.codegalaxy.preferences.UserPreferences;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageServer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class LSP {
    private LSPClient client;
    private LSPServerManager serverManager;
    private LanguageServer server;
    private Future<Void> listenFuture;

    private final Debouncer debouncer = new Debouncer();

    private final Map<String, Integer> documentVersions = new HashMap<>();
    private final Map<String, String> documentContents = new HashMap<>();

    private static final LSP instance = new LSP();


    public static LSP instance() {
        return instance;
    }

    private LSP() {
        // Private constructor to prevent instantiation
    }

    public void start() throws Exception {
        String workspace = UserPreferences.getInstance().get("workspace");
        if (workspace == null)
            throw new IllegalStateException("Workspace not set in user preferences.");

        serverManager = new LSPServerManager();
        serverManager.startServer(workspace);

        client = new LSPClient();
        Launcher<LanguageServer> launcher = Launcher.createLauncher(
                client, LanguageServer.class,
                serverManager.getInputStream(), serverManager.getOutputStream()
        );
        server = launcher.getRemoteProxy();
        listenFuture = launcher.startListening();


        InitializeParams init = new InitializeParams();

        // Create a workspace folder instead of using rootUri
        WorkspaceFolder workspaceFolder = new WorkspaceFolder();
        Path workspacePath = Paths.get(workspace);

        String uri = workspacePath.toUri().toString();
        workspaceFolder.setUri(uri);
        workspaceFolder.setName(workspacePath.getFileName().toString());
        // Set workspace folders as a list with our workspace
        init.setWorkspaceFolders(Collections.singletonList(workspaceFolder));

        // Set client capabilities
        ClientCapabilities capabilities = new ClientCapabilities();

        TextDocumentClientCapabilities textDocumentCapabilities = new TextDocumentClientCapabilities();
        DocumentSymbolCapabilities documentSymbolCapabilities = new DocumentSymbolCapabilities();
        documentSymbolCapabilities.setHierarchicalDocumentSymbolSupport(true);
        textDocumentCapabilities.setDocumentSymbol(documentSymbolCapabilities);

        capabilities.setTextDocument(textDocumentCapabilities);
        init.setCapabilities(capabilities);

        server.initialize(init).get();
        server.initialized(new InitializedParams());

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void openFile(String filePath) throws Exception {
        System.out.println("Opening file: " + filePath);
        Path file = Paths.get(filePath);
        String text = Files.readString(file);
        String uri = file.toUri().toString();
        TextDocumentItem item = new TextDocumentItem(uri, "java", 1, text);
        server.getTextDocumentService().didOpen(new DidOpenTextDocumentParams(item));
        documentVersions.put(uri, 1);
        documentContents.put(uri, text);
    }

    public void closeFile(String filePath) {
        String uri = Paths.get(filePath).toUri().toString();
        if (!documentVersions.containsKey(uri)) {
            System.out.println("File not opened: " + uri);
            return;
        }
        server.getTextDocumentService().didClose(new DidCloseTextDocumentParams(new TextDocumentIdentifier(uri)));
        documentVersions.remove(uri);
        documentContents.remove(uri);
    }

    public void sendChange(String filePath, String newText) throws IllegalStateException  {
        String uri = Paths.get(filePath).toUri().toString();

        if (!documentVersions.containsKey(uri)) {
            throw new IllegalStateException("File must be opened first: " + uri);
        }

        int newVersion = documentVersions.get(uri) + 1;
        documentVersions.put(uri, newVersion);

        VersionedTextDocumentIdentifier docId = new VersionedTextDocumentIdentifier(uri, newVersion);

        TextDocumentContentChangeEvent changeEvent = new TextDocumentContentChangeEvent(newText);

        DidChangeTextDocumentParams changeParams = new DidChangeTextDocumentParams(
                docId,
                Collections.singletonList(changeEvent)
        );

        server.getTextDocumentService().didChange(changeParams);
        documentContents.put(uri, newText);
        System.out.println("Sent change to " + uri);
    }

    public List<CompletionItem> requestCompletions(String filePath, int line, int character) throws Exception {
        String uri = Paths.get(filePath).toUri().toString();

        String text = documentContents.get(uri);
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

        System.out.println("Completions at " + line + ":" + character + ":");
        for (CompletionItem item : items) {
            System.out.printf("  %s → insert: '%s'%n",
                    item.getLabel(),
                    item.getInsertText()
            );
        }

        return items;
    }

    public CompletableFuture<List<DocumentSymbol>> getAllSymbols(String filePath) throws Exception {
        return requestDocumentSymbols(filePath)
                .thenApply(eithers -> {
                    List<DocumentSymbol> flat = new ArrayList<>();
                    for (Either<SymbolInformation, DocumentSymbol> e : eithers) {
                        if (e.isRight()) collect(e.getRight(), flat);
                        else {
                            System.out.println("SymbolInformation: " + e.getLeft().getName());
                        }
                    }
                    return flat;
                });
    }

    private CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> requestDocumentSymbols(String filePath) throws Exception {
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

    public void stop() {
        if (server == null) {
            System.out.println("Server already stopped.");
            return;
        }
        try {
            server.shutdown().get();
            server.exit();

            serverManager.stopServer();
            if (listenFuture != null) {
                listenFuture.cancel(true);
            }
            debouncer.shutdown();
            server = null;
            System.out.println("Server stopped.");
        } catch (Exception e) {
            System.out.println("Error stopping server: " + e.getMessage());
        }
    }



    public void sendChangesDebounce(String filePath, String newText, long delay) throws IllegalStateException {
        debouncer.debounce(() -> sendChange(filePath, newText), delay);
    }

    public Debouncer getDebouncer() {
        return debouncer;
    }

    public LSPClient getClient() {
        return client;
    }


}
