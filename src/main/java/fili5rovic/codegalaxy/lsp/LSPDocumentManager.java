package fili5rovic.codegalaxy.lsp;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.LanguageServer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LSPDocumentManager {
    private final Map<String, Integer> documentVersions = new HashMap<>();
    private final Map<String, String> documentContents = new HashMap<>();

    private LanguageServer server;

    public LSPDocumentManager(LanguageServer server) {
        this.server = server;
    }

    public void openFile(String filePath) throws Exception {
        System.out.println("Opening file: " + filePath);
        Path file = Paths.get(filePath);
        String text = Files.readString(file);
        String uri = file.toUri().toString();
        TextDocumentItem item = new TextDocumentItem(uri, "java", 1, text);
        server.getTextDocumentService().didOpen(new DidOpenTextDocumentParams(item));
        documentVersions.put(uri, 1);
        documentContents.put(uri, text);
    }

    public void closeFile(String filePath) {
        String uri = Paths.get(filePath).toUri().toString();
        if (!documentVersions.containsKey(uri)) {
            System.out.println("File not opened: " + uri);
            return;
        }
        server.getTextDocumentService().didClose(new DidCloseTextDocumentParams(new TextDocumentIdentifier(uri)));
        documentVersions.remove(uri);
        documentContents.remove(uri);
    }

    public void sendChange(String filePath, String newText) throws IllegalStateException  {
        String uri = Paths.get(filePath).toUri().toString();

        if (!documentVersions.containsKey(uri)) {
            throw new IllegalStateException("File must be opened first: " + uri);
        }

        int newVersion = documentVersions.get(uri) + 1;
        documentVersions.put(uri, newVersion);

        VersionedTextDocumentIdentifier docId = new VersionedTextDocumentIdentifier(uri, newVersion);

        TextDocumentContentChangeEvent changeEvent = new TextDocumentContentChangeEvent(newText);

        DidChangeTextDocumentParams changeParams = new DidChangeTextDocumentParams(
                docId,
                Collections.singletonList(changeEvent)
        );

        server.getTextDocumentService().didChange(changeParams);
        documentContents.put(uri, newText);
        System.out.println("Sent change to " + uri);
    }

    public Map<String, String> getDocumentContents() {
        return documentContents;
    }
    public Map<String, Integer> getDocumentVersions() {
        return documentVersions;
    }
}
