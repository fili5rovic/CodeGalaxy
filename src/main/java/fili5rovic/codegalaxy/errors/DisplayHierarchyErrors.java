package fili5rovic.codegalaxy.errors;

import fili5rovic.codegalaxy.dashboardHelper.ProjectManager;
import fili5rovic.codegalaxy.lsp.diagnostics.DiagnosticsListener;
import fili5rovic.codegalaxy.lsp.diagnostics.DiagnosticsPublisher;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.PublishDiagnosticsParams;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DisplayHierarchyErrors implements DiagnosticsListener {

    public void init() {
        DiagnosticsPublisher.instance().subscribe(this);
    }

    @Override
    public void onDiagnosticsUpdated(String uri, PublishDiagnosticsParams params) {
        if (params == null || params.getDiagnostics() == null || params.getDiagnostics().isEmpty()) {
            System.out.println("clearing errors for: " + uri);
            ProjectManager.errorOnPath(Paths.get(URI.create(uri)), false);
            return;
        }

        boolean hasErrors = params.getDiagnostics().stream().anyMatch(d -> d.getSeverity() == DiagnosticSeverity.Error);

        Path filePath = Paths.get(URI.create(uri));
        ProjectManager.errorOnPath(filePath, hasErrors);
    }
}
