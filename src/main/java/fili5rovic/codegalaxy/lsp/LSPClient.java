package fili5rovic.codegalaxy.lsp;

import fili5rovic.codegalaxy.eventBus.EventBus;
import fili5rovic.codegalaxy.eventBus.myEvents.EventLSPReady;
import fili5rovic.codegalaxy.lsp.diagnostics.DiagnosticsPublisher;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.services.LanguageClient;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

class LSPClient implements LanguageClient {

    @Override
    public void telemetryEvent(Object object) {
    }


    @Override
    public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
        DiagnosticsPublisher.instance().publish(diagnostics.getUri(), diagnostics);

//        if (diagnostics.getDiagnostics().isEmpty())
//            return;
//
//        System.out.println("Diagnostics : " + diagnostics.getUri().split("///")[1] + ":");
//        diagnostics.getDiagnostics().forEach(d -> {
//            System.out.println("\t[" + d.getSeverity() + "] : " + d.getMessage());
//            if (d.getSeverity() == DiagnosticSeverity.Error)
//                System.out.println("\t\tRange: " + d.getRange().getStart().getLine() + ":" + d.getRange().getStart().getCharacter() + " - " + d.getRange().getEnd().getLine() + ":" + d.getRange().getEnd().getCharacter());
//        });

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

                if(type.equals("ServiceReady")) {
                    CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {
                        EventBus.instance().publish(new EventLSPReady());
                    });
                }

            }
        } catch (Exception e) {
            // Fallback if parsing fails
            System.out.println("Language status: " + status);
        }
    }

}