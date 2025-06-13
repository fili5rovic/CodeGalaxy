package fili5rovic.codegalaxy.vcs.treeView;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.window.Window;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

public class GitHierarchy extends TreeView<GitTreeItem> {

    private final TreeItem<GitTreeItem> changes;

    private final TreeItem<GitTreeItem> untracked;

    public GitHierarchy() {
        super();
        setShowRoot(false);
        setEditable(false);

        TreeItem<GitTreeItem> root = new TreeItem<>(new GitTreeItem("Root"));
        root.setExpanded(true);
        setRoot(root);

        changes = new TreeItem<>(new GitTreeItem("Changes"));
        changes.setExpanded(true);
        root.getChildren().add(changes);

        untracked = new TreeItem<>(new GitTreeItem("Untracked"));
        untracked.setExpanded(true);
        root.getChildren().add(untracked);


        listeners();
    }

    public void update() {

    }

    private void listeners() {
        setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(GitTreeItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setText(item.getName());
                    setGraphic(item.getValue());
                }
            }
        });

    }

    private void addChange(GitTreeItem item) {
        changes.getChildren().add(new TreeItem<>(item));
    }


    public static void addHierarchy() {
        BorderPane pane = ((DashboardController) Window.getController(Window.WINDOW_DASHBOARD)).getGitTreeViewPane();
        pane.setCenter(new GitHierarchy());
    }

}
