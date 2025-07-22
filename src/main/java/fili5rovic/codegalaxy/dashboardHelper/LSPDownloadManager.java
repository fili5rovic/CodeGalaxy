package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.lsp.LSP;
import fili5rovic.codegalaxy.notification.NotificationManager;
import fili5rovic.codegalaxy.util.downloader.JDTLSDownloadTask;
import fili5rovic.codegalaxy.util.downloader.JDTLSRelease;
import javafx.application.Platform;

import java.util.concurrent.ExecutionException;

public class LSPDownloadManager {

    private static JDTLSDownloadTask lspDownloadTask;

    public static void verifyAndRunLSP() {
        System.out.println("Verifying LSP installation...");
        if (lspExists()) {
            run();
        } else {
            System.err.println("Failed to download or verify LSP.");
        }
    }

    private static void run() {
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

    private static boolean lspExists() {
        if (lspDownloadTask != null && lspDownloadTask.isValidExistingExtraction()) {
            return true;
        }

        lspDownloadTask = new JDTLSDownloadTask(JDTLSRelease.V1_48_0);
        if (lspDownloadTask.isValidExistingExtraction()) {
            return true;
        }

        System.out.println("LSP not found, downloading...");
        NotificationManager.showProgress("Downloading LSP", "Downloading the LSP server, please wait...", lspDownloadTask);

        Thread downloadThread = new Thread(lspDownloadTask);
        downloadThread.setDaemon(false);
        downloadThread.start();

        try {
            Object result = lspDownloadTask.get();
            System.out.println("LSP download completed with result: " + result);
            return lspDownloadTask.isValidExistingExtraction();
        } catch (InterruptedException e) {
            System.err.println("LSP download was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        } catch (ExecutionException e) {
            System.err.println("LSP download failed: " + e.getCause());
            return false;
        }
    }
}
