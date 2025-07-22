package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.lsp.LSP;
import fili5rovic.codegalaxy.notification.NotificationManager;
import fili5rovic.codegalaxy.util.downloader.JDTLSDownloadTask;
import fili5rovic.codegalaxy.util.downloader.JDTLSRelease;
import javafx.application.Platform;

import java.util.concurrent.CompletableFuture;

public class LSPDownloadManager {

    private final JDTLSDownloadTask lspDownloadTask;

    public LSPDownloadManager() {
        lspDownloadTask = new JDTLSDownloadTask(JDTLSRelease.V1_48_0);
    }

    public void verifyAndRunLSP() {
        if(LSP.instance().isStarted())
            return;

        if (lspDownloadTask.isValidExistingExtraction()) {
            run();
            return;
        }

        System.out.println("LSP not found, downloading...");
        NotificationManager.showProgress("Downloading LSP", "Downloading the LSP server, please wait...", lspDownloadTask);

        CompletableFuture.runAsync(lspDownloadTask)
                .thenRun(() -> {
                    Platform.runLater(() -> {
                        System.out.println("LSP download completed successfully.");
                        if (lspDownloadTask.isValidExistingExtraction()) {
                            run();
                        }
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        System.err.println("LSP download failed: " + throwable.getMessage());
                    });
                    return null;
                });
    }

    private static void run() {
        System.out.println("Running LSP server...");
        ProjectManager.checkForValidWorkspace().thenAcceptAsync(success -> {
            if (!success) {
                System.err.println("Fatal error: No valid workspace found. Please set a valid workspace path in properties.");
                Platform.exit();
                return;
            }

            try {
                LSP.instance().start();
                Platform.runLater(ProjectManager::tryToOpenLastProject);
            } catch (Exception e) {
                System.err.println("Failed to start LSP server: " + e.getMessage());
                System.err.println("Fatal error: LSP server is not running. Please check your configuration.");
            }
        });
    }
}
