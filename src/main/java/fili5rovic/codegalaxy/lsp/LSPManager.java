package fili5rovic.codegalaxy.lsp;

import fili5rovic.codegalaxy.preferences.UserPreferences;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageServer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class LSPManager {
    private LSPClient client;
    private LSPServerManager serverManager;
    private LanguageServer server;
    private Future<Void> listenFuture;

    private final Debouncer debouncer = new Debouncer();

    private final Map<String, Integer> documentVersions = new HashMap<>();
    private final Map<String, String> documentContents = new HashMap<>();

    private static final LSPManager instance = new LSPManager();


    public static LSPManager getInstance() {
        return instance;
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
        String uri = Paths.get(workspace).toUri().toString();
        workspaceFolder.setUri(uri);
        workspaceFolder.setName(Paths.get(workspace).getFileName().toString());
        // Set workspace folders as a list with our workspace
        init.setWorkspaceFolders(Collections.singletonList(workspaceFolder));

        // Set client capabilities
        ClientCapabilities capabilities = new ClientCapabilities();
        init.setCapabilities(capabilities);

        server.initialize(init).get();
        server.initialized(new InitializedParams());

//        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void openFile(String filePath) throws Exception {
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

        // Increment document version
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
        System.out.println("Character " + character + ": " + lineText.charAt(character));

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

    public void stop() {
        if (server == null) {
            System.out.println("Server is already stopped.");
            return;
        }
        try {
            server.shutdown().get();
            server.exit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        serverManager.stopServer();
        if (listenFuture != null) {
            listenFuture.cancel(true);
        }
        debouncer.shutdown();
        server = null;
        System.out.println("Server stopped.");
    }

    public Debouncer getDebouncer() {
        return debouncer;
    }

    public static void main(String[] args) {
        try {
            LSPManager manager = new LSPManager();
            manager.start();
            String filePath = "D:\\MY_WORKSPACE\\Sex\\src\\Main.java";

            manager.openFile(filePath);
            manager.requestCompletions(filePath, 3, 15);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
