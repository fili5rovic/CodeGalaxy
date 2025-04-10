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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class LSPManager {
    private LSPClient client;
    private LSPServerManager serverManager;
    private LanguageServer server;
    private Future<Void> listenFuture;

    public static void main(String[] args) {
        try {
            LSPManager manager = new LSPManager();
            manager.start();

            manager.stop();

//            System.out.println("Opening file...");
//            manager.openFile("D:\\MY_WORKSPACE\\Test\\src\\Main.java");
//            System.out.println("File opened successfully.");
//            manager.requestCompletions(
//                    "D:\\MY_WORKSPACE\\Test\\src\\Main.java",
//                    4, 10
//            );

        } catch (Exception e) {
            e.printStackTrace();
        }
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

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void openFile(String filePath) throws Exception {
        Path file = Paths.get(filePath);
        String text = Files.readString(file);
        String uri = file.toUri().toString();
        TextDocumentItem item = new TextDocumentItem(uri, "java", 1, text);
        server.getTextDocumentService().didOpen(new DidOpenTextDocumentParams(item));
    }

    public void requestCompletions(String filePath, int line, int character) throws Exception {
        String uri = Paths.get(filePath).toUri().toString();
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
                    item.getInsertText() != null ? item.getInsertText() : item.getTextEdit() != null
                            ? item.getTextEdit().get()
                            : item.getLabel()
            );
        }
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
        server = null;
        System.out.println("Server stopped.");
    }
}
