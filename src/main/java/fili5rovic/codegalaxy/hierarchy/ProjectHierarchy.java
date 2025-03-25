package fili5rovic.codegalaxy.hierarchy;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ProjectHierarchy extends TreeView<Label> {

    private final String path;

    private ContextMenu contextMenu;

    private ContextMenuHelper contextMenuHelper;

    public ProjectHierarchy(String path) {
        this.path = path;
        this.contextMenu = new ContextMenu();
        this.contextMenuHelper = new ContextMenuHelper();
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        loadHierarchy();

        setupContextMenu();
    }

    private void setupContextMenu() {
        setOnContextMenuRequested(this::contextMenuPopUp);
        setOnMouseClicked(e -> contextMenu.hide());
    }

    private void contextMenuPopUp(ContextMenuEvent e) {
        contextMenu.getItems().clear();

        ObservableList<TreeItem<Label>> selectedItems = this.getSelectionModel().getSelectedItems();
        ArrayList<ProjectItem> items = selectedItems.stream().map(item -> (ProjectItem) item).collect(Collectors.toCollection(ArrayList::new));
        if(items.isEmpty())
            return;

        contextMenu.getItems().addAll(contextMenuHelper.createMenuItems(items));
        contextMenu.show(this, e.getScreenX(), e.getScreenY());
    }

    public void loadHierarchy() {
        Path filePath = Paths.get(path);

        ProjectItem rootItem = new ProjectItem(filePath);
        rootItem.setExpanded(true);

        populateTreeItem(rootItem, filePath);
        this.setRoot(rootItem);
    }

    private void populateTreeItem(TreeItem<Label> parentItem, Path path) {
        try {
            Files.list(path).forEach(p -> {
                ProjectItem item = new ProjectItem(p);

                if (Files.isDirectory(p)) {
                    populateTreeItem(item, p);
                }

                parentItem.getChildren().add(item);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}