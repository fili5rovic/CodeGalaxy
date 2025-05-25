package fili5rovic.codegalaxy.lsp.diagnostics;

import org.eclipse.lsp4j.PublishDiagnosticsParams;

public interface DiagnosticsListener {
    void onDiagnosticsUpdated(String uri, PublishDiagnosticsParams params);
}
