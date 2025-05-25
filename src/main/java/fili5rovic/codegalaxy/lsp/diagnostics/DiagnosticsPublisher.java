package fili5rovic.codegalaxy.lsp.diagnostics;

import org.eclipse.lsp4j.PublishDiagnosticsParams;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DiagnosticsPublisher {

    private static final DiagnosticsPublisher instance = new DiagnosticsPublisher();

    private final List<DiagnosticsListener> diagnostics = new CopyOnWriteArrayList<>();

    private DiagnosticsPublisher() {}

    public static DiagnosticsPublisher instance() {
        return instance;
    }

    public void subscribe(DiagnosticsListener listener) {
        diagnostics.add(listener);
    }

    public void unsubscribe(DiagnosticsListener listener) {
        diagnostics.remove(listener);
    }

    public void publish(String uri, PublishDiagnosticsParams params) {
        for (DiagnosticsListener listener : diagnostics) {
            listener.onDiagnosticsUpdated(uri, params);
        }
    }
}
