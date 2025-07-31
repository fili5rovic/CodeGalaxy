package fili5rovic.codegalaxy.errors;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.controller.Controllers;
import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.lsp.diagnostics.DiagnosticsListener;
import fili5rovic.codegalaxy.lsp.diagnostics.DiagnosticsPublisher;
import fili5rovic.codegalaxy.lsp.diagnostics.ErrorItem;
import fili5rovic.codegalaxy.util.SVGUtil;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import org.eclipse.lsp4j.PublishDiagnosticsParams;

import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;

public class DisplayErrorsHandler implements DiagnosticsListener {

    private static DashboardController controller;

    private final HashMap<String, PublishDiagnosticsParams> paramsMap = new HashMap<>();

    public void init() {
        controller = Controllers.dashboardController();
        DiagnosticsPublisher.instance().subscribe(this);
        DisplayHierarchyErrors hierarchyErrors = new DisplayHierarchyErrors();
        hierarchyErrors.init();
    }

    @Override
    public void onDiagnosticsUpdated(String uri, PublishDiagnosticsParams params) {
        paramsMap.put(uri, params);
        CodeGalaxy openCodeGalaxy = controller.getCurrentOpenCodeGalaxy();
        if(controller == null || openCodeGalaxy == null) {
            return;
        }
        if (uri.equals(openCodeGalaxy.getFilePath().toUri().toString())) {
            Platform.runLater(this::displayErrors);
        }
    }

    public void displayErrors() {
        VBox errorVBox = controller.getErrorVBox();
        errorVBox.getChildren().clear();


        CodeGalaxy openCodeGalaxy = controller.getCurrentOpenCodeGalaxy();
        if (openCodeGalaxy == null) {
            errorVBox.getChildren().add(new Label("No file opened"));
            return;
        }
        Path openedPath = openCodeGalaxy.getFilePath();
        if (openedPath == null) {
            errorVBox.getChildren().add(new Label("No file opened"));
            return;
        }

        PublishDiagnosticsParams params = paramsMap.get(openedPath.toUri().toString());
        if (params == null)
            return;

        URI uri = URI.create(params.getUri());
        Path filePath = Path.of(uri);

        errorVBox.getChildren().add(titleDisplay(filePath));
        params.getDiagnostics().forEach(diagnostic -> {
            ErrorItem errorItem = new ErrorItem(diagnostic);
            errorVBox.getChildren().add(errorItem);
        });


    }

    private HBox titleDisplay(Path filePath) {
        HBox titleBox = new HBox();
        titleBox.setSpacing(10);

        Label fileLabel = new Label(filePath.getFileName().toString());
        fileLabel.setGraphic(SVGUtil.getIconByPath(filePath, 20, 0));
        fileLabel.setFont(new Font(20));

        titleBox.getChildren().add(fileLabel);

        Label pathLabel = new Label(filePath.toString());
        pathLabel.setFont(new Font(20));
        pathLabel.getStyleClass().add("label-secondary");

        titleBox.getChildren().add(pathLabel);
        titleBox.setPadding(new javafx.geometry.Insets(5, 0, 2, 10));


        return titleBox;
    }
}
