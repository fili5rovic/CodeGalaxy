package fili5rovic.codegalaxy.hierarchy;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.window.Window;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ProjectHierarchy extends TreeView<Label> {

    private final String path;

    private final ContextMenu contextMenu;
    private final ContextMenuHelper contextMenuHelper;

    private static final DashboardController controller = ((DashboardController) Window.getController(Window.WINDOW_DASHBOARD));

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
        setOnMouseClicked(this::handleMouseClick);
    }

    private void handleMouseClick(MouseEvent e) {
        contextMenu.hide();

        if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
            ProjectItem item = (ProjectItem) this.getSelectionModel().getSelectedItem();
            if(item == null || Files.isDirectory(item.getPath()))
                return;
            controller.createTab(item.getPath());
        }
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