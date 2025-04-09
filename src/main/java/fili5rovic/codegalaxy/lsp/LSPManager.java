package fili5rovic.codegalaxy.lsp;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageServer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.concurrent.Future;

public class LSPManager {
    private LSPClient client;
    private LSPServerManager serverManager;
    private LanguageServer server;
    private Future<Void> listenFuture;

    public static void main(String[] args) {
        try {
            LSPManager manager = new LSPManager();
            manager.start("D:\\MY_WORKSPACE");

            System.out.println("Opening file...");
            manager.openFile("D:\\MY_WORKSPACE\\Test\\src\\Main.java");
            System.out.println("File opened successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start(String workspacePath) throws Exception {
        serverManager = new LSPServerManager();
        serverManager.startServer(workspacePath);

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
        String uri = Paths.get(workspacePath).toUri().toString();
        workspaceFolder.setUri(uri);
        workspaceFolder.setName(Paths.get(workspacePath).getFileName().toString());

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

    public void stop() {
        try {
            server.shutdown().get();
            server.exit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        serverManager.stopServer();
        System.out.println("Server stopped.");
    }
}
