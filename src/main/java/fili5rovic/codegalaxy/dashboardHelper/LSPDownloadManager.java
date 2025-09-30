package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.eventBus.EventBus;
import fili5rovic.codegalaxy.eventBus.MyListener;
import fili5rovic.codegalaxy.eventBus.myEvents.EventLSPReady;
import fili5rovic.codegalaxy.eventBus.myEvents.MyEvent;
import fili5rovic.codegalaxy.lsp.LSP;
import fili5rovic.codegalaxy.notification.NotificationManager;
import fili5rovic.codegalaxy.util.downloader.JDTLSDownloadTask;
import fili5rovic.codegalaxy.util.downloader.JDTLSRelease;
import fili5rovic.codegalaxy.window.Window;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.concurrent.CompletableFuture;

public class LSPDownloadManager implements MyListener {

    private final JDTLSDownloadTask lspDownloadTask;

    public LSPDownloadManager() {
        lspDownloadTask = new JDTLSDownloadTask(JDTLSRelease.V1_48_0);
        EventBus.instance().register(this, EventLSPReady.class);
    }

    public void verifyAndRunLSP() {
        if (LSP.instance().isStarted()) return;

        if (lspDownloadTask.isValidExistingExtraction()) {
            run();
            return;
        }

        showAlert(Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage());
    }

    private void showAlert(Stage owner) {
        Alert setupAlert = new Alert(Alert.AlertType.CONFIRMATION);
        setupAlert.setTitle("Required Setup");
        setupAlert.setHeaderText("Java Support Setup Required");
        setupAlert.setContentText("""
                To enable Java support, CodeGalaxy needs to download the Eclipse JDT Language Server (~50 MB).
                This is a one-time setup and is required to use the IDE.
                """);
        setupAlert.setGraphic(null);
        ButtonType continueButton = new ButtonType("Continue", ButtonBar.ButtonData.OK_DONE);
        ButtonType exitButton = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);

        setupAlert.getButtonTypes().setAll(continueButton, exitButton);
        if (owner != null) {
            setupAlert.initOwner(owner);
            if (owner.getScene() != null) {
                setupAlert.getDialogPane().getStylesheets().addAll(owner.getScene().getStylesheets());
            }
            if (!owner.getIcons().isEmpty()) {
                ((Stage) setupAlert.getDialogPane().getScene().getWindow()).getIcons().addAll(owner.getIcons());
            }
        } else {
            System.err.println("Owner stage is null, cannot set stylesheets for the alert dialog. Also not setting owner for the alert dialog.");
        }
        setupAlert.showAndWait().ifPresent(response -> {
            if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                download();
            } else {
                System.out.println("User declined LSP download. Exiting.");
                Platform.exit();
            }
        });
    }

    private void download() {
        System.out.println("LSP not found, downloading...");
        NotificationManager.showProgress("Downloading LSP", "Downloading the LSP server, please wait...", lspDownloadTask);

        CompletableFuture.runAsync(lspDownloadTask).thenRun(() -> Platform.runLater(() -> {
            System.out.println("LSP download completed successfully.");
            if (lspDownloadTask.isValidExistingExtraction()) {
                run();
            }
        })).exceptionally(throwable -> {
            Platform.runLater(() -> System.err.println("LSP download failed: " + throwable.getMessage()));
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
            } catch (Exception e) {
                System.err.println("Failed to start LSP server: " + e.getMessage());
                System.err.println("Fatal error: LSP server is not running. Please check your configuration.");
            }
        });
    }

    @Override
    public void handle(MyEvent e) {
        Platform.runLater(ProjectManager::tryToOpenLastProject);
    }
}
