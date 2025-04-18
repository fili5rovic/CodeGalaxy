package fili5rovic.codegalaxy.lsp;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.services.LanguageClient;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

class LSPClient implements LanguageClient {
    @Override
    public void telemetryEvent(Object object) {
    }

    @Override
    public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
        System.out.println("[DIAGNOSTICS] : " + diagnostics.getUri().split("///")[1] + ":");
        diagnostics.getDiagnostics().forEach(d ->
                System.out.println("\t[" + d.getSeverity() + "] : " + d.getMessage()));
    }


    @Override
    public void showMessage(MessageParams messageParams) {
        System.out.println("[SERVER-MESSAGE]: " + messageParams.getMessage());
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
            if (status instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> statusMap = (Map<String, Object>) status;

                String type = statusMap.get("type") != null ? statusMap.get("type").toString() : "Unknown";
                String message = statusMap.get("message") != null ? statusMap.get("message").toString() : "";

                System.out.println("[LSP-" + type + "]: " + message);

            }
        } catch (Exception e) {
            // Fallback if parsing fails
            System.out.println("Language status: " + status);
        }
    }

}