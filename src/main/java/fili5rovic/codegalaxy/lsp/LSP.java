package fili5rovic.codegalaxy.lsp;

import fili5rovic.codegalaxy.dashboardHelper.MenuManager;
import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.util.Debouncer;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageServer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class LSP {   //TODO Ability to change workspace without restarting
    private LSPServerManager serverManager;
    private LanguageServer server;
    private Future<Void> listenFuture;

    private final Debouncer debouncer = new Debouncer();

    private static final LSP instance = new LSP();

    private LSPDocumentManager documentManager;
    private LSPRequestManager requestManager;
    private LSPRefactorManager refactorManager;


    public static LSP instance() {
        return instance;
    }

    private LSP() {
        // Private constructor to prevent instantiation
    }

    public void start() throws Exception {
        cleanTemporaryFiles();

        String workspace = IDESettings.getInstance().get("workspace");
        if (workspace == null)
            throw new IllegalStateException("Workspace not set in properties.");

        serverManager = new LSPServerManager();
        serverManager.startServer(workspace);

        LSPClient client = new LSPClient();
        Launcher<LanguageServer> launcher = Launcher.createLauncher(
                client, LanguageServer.class,
                serverManager.getInputStream(), serverManager.getOutputStream()
        );
        server = launcher.getRemoteProxy();
        listenFuture = launcher.startListening();

        InitializeParams init = new InitializeParams();

        WorkspaceFolder workspaceFolder = new WorkspaceFolder();
        Path workspacePath = Paths.get(workspace);

        String uri = workspacePath.toUri().toString();
        workspaceFolder.setUri(uri);
        workspaceFolder.setName(workspacePath.getFileName().toString());
        init.setWorkspaceFolders(Collections.singletonList(workspaceFolder));
        init.setCapabilities(createClientCapabilities());
        server.initialize(init).get();
        server.initialized(new InitializedParams());

        afterServerStart();

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    private ClientCapabilities createClientCapabilities() {
        ClientCapabilities capabilities = new ClientCapabilities();
        TextDocumentClientCapabilities textDocumentCapabilities = new TextDocumentClientCapabilities();

        DocumentSymbolCapabilities documentSymbolCapabilities = new DocumentSymbolCapabilities();
        documentSymbolCapabilities.setHierarchicalDocumentSymbolSupport(true);
        textDocumentCapabilities.setDocumentSymbol(documentSymbolCapabilities);

        RenameCapabilities renameCapabilities = new RenameCapabilities();
        renameCapabilities.setPrepareSupport(true);
        textDocumentCapabilities.setRename(renameCapabilities);

        capabilities.setTextDocument(textDocumentCapabilities);

        WorkspaceClientCapabilities workspaceCapabilities = new WorkspaceClientCapabilities();

        WorkspaceEditCapabilities workspaceEditCapabilities = new WorkspaceEditCapabilities();
        workspaceEditCapabilities.setDocumentChanges(true);
        workspaceEditCapabilities.setResourceOperations(Arrays.asList("create", "rename", "delete"));
        workspaceCapabilities.setWorkspaceEdit(workspaceEditCapabilities);

        FileOperationsWorkspaceCapabilities fileOperations = new FileOperationsWorkspaceCapabilities();
        workspaceCapabilities.setFileOperations(fileOperations);

        capabilities.setWorkspace(workspaceCapabilities);

        return capabilities;
    }

    private void afterServerStart() {
        this.documentManager = new LSPDocumentManager(server);
        this.requestManager = new LSPRequestManager(server, documentManager);
        this.refactorManager = new LSPRefactorManager(server);
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
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }

    private void cleanTemporaryFiles() {
        String projectPath = System.getProperty("user.dir");
        File managerFolder = new File(projectPath + "\\lsp\\config_win\\org.eclipse.equinox.app\\.manager");
        if (!managerFolder.exists() || !managerFolder.isDirectory()) {
            System.out.println("Manager folder does not exist or is not a directory.");
            return;
        }

        File[] tmpFiles = managerFolder.listFiles((_, name) -> name.startsWith(".tmp"));

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

    public void sendSave(String filePath) {
        documentManager.sendSave(filePath);
    }

    public List<CompletionItem> requestCompletions(String filePath, int line, int character) throws Exception {
        return requestManager.requestCompletions(filePath, line, character);
    }

    public CompletableFuture<List<DocumentSymbol>> getAllSymbols(String filePath) {
        return requestManager.getAllSymbols(filePath);
    }

    public CompletableFuture<List<? extends TextEdit>> formatDocument(String filePath) {
        return requestManager.formatDocument(filePath);
    }

    public void sendChangesDebounce(String filePath, String newText, long delay) throws IllegalStateException {
        debouncer.debounce(() -> documentManager.sendChange(filePath, newText), delay);
    }

    public void rename(String filePath, int line, int character, String newName) throws Exception {
        MenuManager.saveAllFiles(null);
        refactorManager.rename(filePath, line, character, newName);
    }

    public CompletableFuture<List<? extends Location>> goToDefinition(String filePath, int line, int character) {
        return requestManager.goToDefinition(filePath, line, character);
    }

    public CompletableFuture<List<? extends Location>> references(String filePath, int line, int character) {
        return requestManager.references(filePath, line, character);
    }

    public CompletableFuture<Hover> hover(String filePath, int line, int character) {
        return requestManager.hover(filePath, line, character);
    }

    public void sendFolderChange(String folderPath) {
        documentManager.sendFolderChange(folderPath);
    }

    public Debouncer getDebouncer() {
        return debouncer;
    }

}
