package fili5rovic.codegalaxy.vcs.treeView;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.vcs.GitUtil;
import fili5rovic.codegalaxy.window.Window;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import org.eclipse.jgit.api.Status;

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

    public void update(Status status) {
        untracked.getChildren().clear();
        status.getUntracked().forEach(item -> addUntracked(new GitTreeItem(item)));

        if(untracked.getChildren().isEmpty()) {
            getRoot().getChildren().remove(untracked);
        } else if(!getRoot().getChildren().contains(untracked)) {
            getRoot().getChildren().add(untracked);
        }

        changes.getChildren().clear();
        status.getChanged().forEach(item -> addChange(new GitTreeItem(item)));

        if(changes.getChildren().isEmpty()) {
            getRoot().getChildren().remove(changes);
        } else if(!getRoot().getChildren().contains(changes)) {
            getRoot().getChildren().add(changes);
        }
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
                    setGraphic(item.getToggle());
                }
            }
        });
        untracked.getValue().getToggle().selectedProperty().addListener((_, _, selected) -> expandChildren(untracked, selected));
    }

    private void expandChildren(TreeItem<GitTreeItem> item, boolean selected) {
        if (item != null) {
            item.getValue().getToggle().setSelected(selected);
            for (TreeItem<GitTreeItem> child : item.getChildren()) {
                expandChildren(child, selected);
            }
        }
    }

    private void addChange(GitTreeItem item) {
        changes.getChildren().add(new TreeItem<>(item));
    }

    private void addUntracked(GitTreeItem item) {
        untracked.getChildren().add(new TreeItem<>(item));
    }


    public static void addHierarchy() {
        BorderPane pane = ((DashboardController) Window.getController(Window.WINDOW_DASHBOARD)).getGitTreeViewPane();
        GitHierarchy gitHierarchy = new GitHierarchy();
        GitUtil.instance().setHierarchy(gitHierarchy);
        pane.setCenter(gitHierarchy);
    }

}
