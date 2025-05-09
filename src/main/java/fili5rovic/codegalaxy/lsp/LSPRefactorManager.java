package fili5rovic.codegalaxy.lsp;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageServer;

import java.nio.file.Paths;
import java.util.List;
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
            System.out.println("Workspace Edit:\n" + edit);

        } else {
            List<Command> commands = result.getRight();
            for (Command command : commands) {
//                command.execute();
                System.out.println("Command: " + command.getTitle());
            }
        }
    }
}
