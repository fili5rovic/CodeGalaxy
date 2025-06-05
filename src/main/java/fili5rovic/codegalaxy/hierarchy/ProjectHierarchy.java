package fili5rovic.codegalaxy.hierarchy;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.lsp.diagnostics.DiagnosticsListener;
import fili5rovic.codegalaxy.lsp.diagnostics.DiagnosticsPublisher;
import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.window.Window;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.PublishDiagnosticsParams;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectHierarchy extends TreeView<Label> {

    private final Path filePath;

    private final ContextMenu contextMenu;
    private final ContextMenuHelper contextMenuHelper;

    private static final DashboardController controller = ((DashboardController) Window.getController(Window.WINDOW_DASHBOARD));

    private List<String> expandedPaths;

    private final HashSet<ProjectItem> javaFiles = new HashSet<>();

    public ProjectHierarchy(Path path) {
        this.filePath = path;
        this.contextMenu = new ContextMenu();
        this.contextMenuHelper = new ContextMenuHelper();
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        reloadHierarchy();
        setupContextMenu();
    }

    public void reloadHierarchy() {
        ProjectItem rootItem = new ProjectItem(filePath);
        this.setRoot(rootItem);
        reloadHierarchy(rootItem);
    }

    public void reloadHierarchy(ProjectItem item) {
        this.expandedPaths = IDESettings.getInstance().getMultiple("expanded");
        this.javaFiles.clear();

        item.getChildren().clear();
        item.setExpanded(true);
        populateTreeItem(item, item.getPath());
    }

    public void errorOnPath(Path path, boolean error) {
        for (ProjectItem javaFile : javaFiles) {
            if (javaFile.getPath().equals(path)) {
                javaFile.error(error);
                break;
            }
        }
    }

    private void restoreExpandedState(ProjectItem item, List<String> expandedPaths) {
        if (expandedPaths.contains(item.getPath().toString())) {
            item.setExpanded(true);
        }
    }

    private void setupContextMenu() {
        setOnContextMenuRequested(this::contextMenuPopup);
        setOnMouseClicked(this::handleMouseClick);
    }

    private void handleMouseClick(MouseEvent e) {
        contextMenu.hide();
        if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
            ProjectItem item = (ProjectItem) this.getSelectionModel().getSelectedItem();
            if (item == null || Files.isDirectory(item.getPath())) return;
            controller.createTab(item.getPath());
        }
    }

    private void contextMenuPopup(ContextMenuEvent e) {
        contextMenu.getItems().clear();

        ObservableList<TreeItem<Label>> selectedItems = this.getSelectionModel().getSelectedItems();
        ArrayList<ProjectItem> items = selectedItems.stream().map(item -> (ProjectItem) item).collect(Collectors.toCollection(ArrayList::new));
        if (items.isEmpty()) return;

        contextMenu.getItems().addAll(contextMenuHelper.createMenuItems(items));
        contextMenu.show(this, e.getScreenX(), e.getScreenY());
    }

    private void populateTreeItem(TreeItem<Label> parentItem, Path path) {
        try {
            Files.list(path).forEach(p -> {
                ProjectItem item = new ProjectItem(p);

                if (Files.isDirectory(p)) {
                    populateTreeItem(item, p);
                    restoreExpandedState(item, expandedPaths);
                } else if (p.toString().endsWith(".java")) {
                    javaFiles.add(item);
                }
                parentItem.getChildren().add(item);
            });
        } catch (IOException e) {
            System.err.println("Failed to load project hierarchy: " + e.getMessage());
        }
    }


}