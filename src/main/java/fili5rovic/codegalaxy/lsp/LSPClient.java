package fili5rovic.codegalaxy.lsp;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LSPClient implements LanguageClient {
    @Override
    public void telemetryEvent(Object object) {
    }

    @Override
    public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
        System.out.println("Received diagnostics:");
        diagnostics.getDiagnostics().forEach(d ->
                System.out.println(" - " + d.getMessage()));
    }

    @Override
    public void showMessage(MessageParams messageParams) {
        System.out.println("Server message: " + messageParams.getMessage());
    }

    @Override
    public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
        return null;
    }

    @Override
    public void logMessage(MessageParams messageParams) {
        System.out.println("Server log: " + messageParams.getMessage());
    }


    public static void main(String[] args) throws Exception {
        // 1) start server
        System.setProperty("org.eclipse.lsp4j.jsonrpc.trace", "false");
        LSPServerManager mgr = new LSPServerManager();
        mgr.startServer("C:\\Users\\fili5\\OneDrive\\Desktop\\test\\workspace");

        // 2) wire up LSP4J
        LSPClient client = new LSPClient();
        Launcher<LanguageServer> launcher = Launcher.createLauncher(
                client, LanguageServer.class,
                mgr.getInputStream(), mgr.getOutputStream()
        );

        LanguageServer server = launcher.getRemoteProxy();
        Future<Void> listenFuture = launcher.startListening();

        // 3) initialize
        InitializeParams init = new InitializeParams();
        init.setRootUri(Paths.get("C:\\Users\\fili5\\OneDrive\\Desktop\\test\\workspace")
                .toUri().toString());
        InitializeResult result = server.initialize(init).get();
        System.out.println("Server initialized!");

        // 4) notify initialized
        server.initialized(new InitializedParams());

        // 5) open a document
        Path file = Paths.get("C:\\Users\\fili5\\OneDrive\\Desktop\\test\\workspace\\project\\src\\main.java");
        String text = Files.readString(file);
        String uri  = file.toUri().toString();
        TextDocumentItem item = new TextDocumentItem(uri, "java", 1, text);
        server.getTextDocumentService()
                .didOpen(new DidOpenTextDocumentParams(item));

        // 6) stay alive until the streams close
        listenFuture.get();

        // (optional) shutdown hook will destroy the process
        Runtime.getRuntime().addShutdownHook(new Thread(mgr::stopServer));
    }

}