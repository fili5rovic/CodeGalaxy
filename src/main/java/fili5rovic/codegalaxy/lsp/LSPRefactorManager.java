package fili5rovic.codegalaxy.lsp;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.window.Window;
import javafx.scene.control.Tab;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageServer;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

class LSPRefactorManager {

    private final LanguageServer server;

    LSPRefactorManager(LanguageServer server) {
        this.server = server;
    }

    void rename(String filePath, int line, int character, String newName) throws Exception {
        String uri = Paths.get(filePath).toUri().toString();
        TextDocumentIdentifier docId = new TextDocumentIdentifier(uri);
        Position pos = new Position(line, character);
        RenameParams params = new RenameParams(docId, pos, newName);

        CompletableFuture<WorkspaceEdit> future =
                server.getTextDocumentService().rename(params);

        CompletableFuture<Either<WorkspaceEdit, List<Command>>> wrappedFuture =
                future.thenApply(edit -> Either.forLeft(edit));

        Either<WorkspaceEdit, List<Command>> result = wrappedFuture.get();


        if (result.isLeft()) {
            WorkspaceEdit edit = result.getLeft();
            handleRename(edit);

        } else {
            List<Command> commands = result.getRight();
            for (Command command : commands) {
                System.out.println("Command: " + command.getTitle());
            }
        }
    }

    private void handleRename(WorkspaceEdit workspaceEdit) {
        DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);
        if (workspaceEdit.getDocumentChanges() != null) {
            for (Either<TextDocumentEdit, ResourceOperation> docChange : workspaceEdit.getDocumentChanges()) {
                if (docChange.isRight()) {
                    ResourceOperation op = docChange.getRight();
                    if (op instanceof RenameFile renameOp) {
                        String oldUri = renameOp.getOldUri();
                        String newUri = renameOp.getNewUri();
                        System.out.println("File rename: " + oldUri + " -> " + newUri);

                        try {
                            Path oldPath = new File(URI.create(oldUri)).toPath();
                            Path newPath = new File(URI.create(newUri)).toPath();

                            Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("File renamed successfully");

                            for (Tab t : controller.getTabPane().getTabs()) {
                                CodeGalaxy content = (CodeGalaxy) t.getContent();
                                URI uriKey = URI.create(oldUri);
                                URI uriFilePath = content.getFilePath().toUri();

                                if (uriKey.equals(uriFilePath)) {
                                    content.setFile(newPath);
                                    t.setText(newPath.getFileName().toString());
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Error renaming file: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                } else if (docChange.isLeft()) {
                    TextDocumentEdit textDocEdit = docChange.getLeft();
                    VersionedTextDocumentIdentifier docId = textDocEdit.getTextDocument();
                    String uri = docId.getUri();

                    System.out.println("Document edit: " + uri);

                    CodeGalaxy codeGalaxy = null;
                    for (Tab t : controller.getTabPane().getTabs()) {
                        CodeGalaxy content = (CodeGalaxy) t.getContent();
                        URI uriKey = URI.create(uri);
                        URI uriFilePath = content.getFilePath().toUri();

                        if (uriKey.equals(uriFilePath)) {
                            codeGalaxy = content;
                            break;
                        }
                    }
                    if (codeGalaxy == null) {
                        System.out.println("Tab not open: " + uri);
                        // change the file directly here
                        return;
                    }

                    for (TextEdit textEdit : textDocEdit.getEdits()) {
                        int startLine = textEdit.getRange().getStart().getLine();
                        int startChar = textEdit.getRange().getStart().getCharacter();
                        int endLine = textEdit.getRange().getEnd().getLine();
                        int endChar = textEdit.getRange().getEnd().getCharacter();

                        int startOffset = codeGalaxy.position(startLine, startChar).toOffset();
                        int endOffset = codeGalaxy.position(endLine, endChar).toOffset();

                        codeGalaxy.replaceText(startOffset, endOffset, textEdit.getNewText());
                    }
                }
            }
        }
    }
}

