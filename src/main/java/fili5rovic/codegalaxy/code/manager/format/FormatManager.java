package fili5rovic.codegalaxy.code.manager.format;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.lsp.LSP;
import javafx.application.Platform;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

public class FormatManager extends Manager {

    private static final int THREAD_DELAY = 500;

    public FormatManager(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    public void init() {
        codeGalaxy.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.isAltDown() && event.getCode() == javafx.scene.input.KeyCode.L) {
                formatDocument();
                event.consume();
            }
        });
    }

    public void formatDocument() {
        String filePath = codeGalaxy.getFilePath().toString();
        LSP.instance().sendSave(filePath);

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(THREAD_DELAY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).thenCompose(ignored -> LSP.instance().formatDocument(filePath))
                .thenAccept(_ -> Platform.runLater(() -> {

            try {
                String text = Files.readString(codeGalaxy.getFilePath());
                if (text.equals(codeGalaxy.getText())) return;

                codeGalaxy.replaceText(0, codeGalaxy.getLength(), text);
                codeGalaxy.selectWord();
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        }));
    }

}
