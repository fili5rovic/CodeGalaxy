package fili5rovic.codegalaxy.lsp;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
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

    @JsonNotification("language/status")
    public void languageStatus(Object status) {
        try {
            // Since status comes as a JSON object, we can use Gson or cast to Map
            if (status instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> statusMap = (Map<String, Object>) status;

                String type = statusMap.get("type") != null ? statusMap.get("type").toString() : "Unknown";
                String message = statusMap.get("message") != null ? statusMap.get("message").toString() : "";

                // Format the output as you prefer
                System.out.println("[LSP-" + type + "]: " + message);

            }
        } catch (Exception e) {
            // Fallback if parsing fails
            System.out.println("Language status: " + status);
        }
    }

}