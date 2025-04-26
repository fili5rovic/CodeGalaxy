package fili5rovic.codegalaxy.lsp;

import fili5rovic.codegalaxy.preferences.UserPreferences;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageServer;

import java.io.File;
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

    private static final LSP instance = new LSP();

    private LSPDocumentManager documentManager;
    private LSPRequestManager requestManager;


    public static LSP instance() {
        return instance;
    }

    private LSP() {
        // Private constructor to prevent instantiation
    }

    public void start() throws Exception {
        cleanTemporaryFiles();

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

        afterServerStart();

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    private void afterServerStart() {
        this.documentManager = new LSPDocumentManager(server);
        this.requestManager = new LSPRequestManager(server, documentManager);
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

    private void cleanTemporaryFiles() {
        String projectPath = System.getProperty("user.dir");
        File managerFolder = new File(projectPath + "\\lsp\\config_win\\org.eclipse.equinox.app\\.manager");
        if (!managerFolder.exists() || !managerFolder.isDirectory()) {
            System.out.println("Manager folder does not exist or is not a directory.");
            return;
        }

        File[] tmpFiles = managerFolder.listFiles((dir, name) -> name.startsWith(".tmp"));

        if (tmpFiles == null) {
            System.out.println("No .tmp files found.");
            return;
        }

        for (File tmpFile : tmpFiles) {
            tmpFile.delete();
        }
    }


    public void openFile(String filePath) throws Exception {
        documentManager.openFile(filePath);
    }

    public void closeFile(String filePath) {
        documentManager.closeFile(filePath);
    }

    public void sendChange(String filePath, String newText) throws IllegalStateException {
        documentManager.sendChange(filePath, newText);
    }

    public List<CompletionItem> requestCompletions(String filePath, int line, int character) throws Exception {
        return requestManager.requestCompletions(filePath, line, character);
    }

    public CompletableFuture<List<DocumentSymbol>> getAllSymbols(String filePath) {
        return requestManager.getAllSymbols(filePath);
    }

    public void sendChangesDebounce(String filePath, String newText, long delay) throws IllegalStateException {
        debouncer.debounce(() -> documentManager.sendChange(filePath, newText), delay);
    }

    public Debouncer getDebouncer() {
        return debouncer;
    }

}
