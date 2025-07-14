package fili5rovic.codegalaxy.vcs.treeView;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.projectSettings.ProjectSettingsUtil;
import fili5rovic.codegalaxy.projectSettings.VCSUtil;
import fili5rovic.codegalaxy.util.SVGUtil;
import fili5rovic.codegalaxy.vcs.GitListenerUtil;
import fili5rovic.codegalaxy.vcs.GitUtil;
import fili5rovic.codegalaxy.window.Window;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.eclipse.jgit.api.Status;

import java.io.IOException;
import java.util.HashSet;

public class GitHierarchy extends TreeView<GitTreeItem> {

    private final TreeItem<GitTreeItem> changes;
    private final TreeItem<GitTreeItem> untracked;

    private final HashSet<String> changesSet = new HashSet<>();

    public GitHierarchy() {
        super();
        setShowRoot(false);
        setEditable(false);

        TreeItem<GitTreeItem> root = new TreeItem<>(new GitTreeItem("Root"));
        root.setExpanded(true);
        setRoot(root);

        changes = new TreeItem<>(new GitTreeItem("Changes"));
        changes.getValue().getToggle().selectedProperty().addListener((_, _, selected) -> GitListenerUtil.toggleListener(selected, getRoot()));

        changes.setExpanded(true);
        root.getChildren().add(changes);

        untracked = new TreeItem<>(new GitTreeItem("Untracked"));
        untracked.getValue().getToggle().selectedProperty().addListener((_, _, selected) -> GitListenerUtil.toggleListener(selected, getRoot()));

        untracked.setExpanded(true);
        root.getChildren().add(untracked);

        listeners();
    }

    public void update(Status status) {
        updateChangesSection(status);
        updateUntrackedSection(status);
        updateTree();
    }

    private void updateTree() {
        updateTreeItemVisibility(changes, 0);
        updateTreeItemVisibility(untracked, getRoot().getChildren().size());
    }

    private void updateChangesSection(Status status) {
        changesSet.clear();
        changes.getChildren().clear();
        changes.getValue().getToggle().setSelected(false);
        status.getModified().forEach(item -> addChange(new GitTreeItem(item).modified()));
        status.getAdded().forEach(item -> addChange(new GitTreeItem(item)));
        status.getChanged().forEach(item -> addChange(new GitTreeItem(item)));
    }

    private void updateUntrackedSection(Status status) {
        untracked.getChildren().clear();
        untracked.getValue().getToggle().setSelected(false);
        status.getUntracked().forEach(item -> addUntracked(new GitTreeItem(item)));
    }

    private void updateTreeItemVisibility(TreeItem<GitTreeItem> item, int targetIndex) {
        boolean hasChildren = !item.getChildren().isEmpty();
        boolean isInTree = getRoot().getChildren().contains(item);

        if (!hasChildren && isInTree) {
            getRoot().getChildren().remove(item);
        } else if (hasChildren && !isInTree) {
            getRoot().getChildren().add(targetIndex, item);
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
                    TreeItem<GitTreeItem> treeItem = getTreeItem();
                    if (treeItem != null && treeItem.getParent() == tv.getRoot()) {
                        setGraphic(item.getToggle());
                    } else if (treeItem != null) {
                        ImageView icon = SVGUtil.getIconByPath(item.getPath(), 20, 20, 2);
                        HBox hbox = new HBox(item.getToggle(), icon);
                        hbox.setSpacing(5);
                        setGraphic(hbox);
                        setGraphicTextGap(0);
                    } else {
                        setGraphic(item.getToggle());
                    }
                }
            }
        });
        untracked.getValue().getToggle().selectedProperty().addListener((_, _, selected) -> applySelectionToChildren(untracked, selected));
        changes.getValue().getToggle().selectedProperty().addListener((_, _, selected) -> applySelectionToChildren(changes, selected));

        GitListenerUtil.applyGitCommitTextListener(getRoot());
    }

    private void applySelectionToChildren(TreeItem<GitTreeItem> item, boolean selected) {
        if (item != null) {
            item.getValue().getToggle().setSelected(selected);
            for (TreeItem<GitTreeItem> child : item.getChildren()) {
                applySelectionToChildren(child, selected);
            }
        }
    }

    private void addChange(GitTreeItem item) {
        if (changesSet.contains(item.getPathGit())) {
            return;
        }
        changesSet.add(item.getPathGit());
        changes.getChildren().add(new TreeItem<>(item));
        item.getToggle().selectedProperty().addListener((_, _, selected) -> GitListenerUtil.toggleListener(selected, getRoot()));
    }

    private void addUntracked(GitTreeItem item) {
        untracked.getChildren().add(new TreeItem<>(item));
        item.getToggle().selectedProperty().addListener((_, _, selected) -> GitListenerUtil.toggleListener(selected, getRoot()));
    }


    public static void addHierarchy() {
        BorderPane pane = ((DashboardController) Window.getController(Window.WINDOW_DASHBOARD)).getGitTreeViewPane();
        GitHierarchy gitHierarchy = new GitHierarchy();
        GitUtil.instance().setHierarchy(gitHierarchy);
        pane.setCenter(gitHierarchy);

        if (ProjectSettingsUtil.isVCSInit()) {
            new Thread(() -> {
                try {
                    GitUtil.instance().open(VCSUtil.readVcsSettings().getRepositoryPath());
                } catch (IOException e) {
                    System.err.println("Failed to open VCS repository: " + e.getMessage());
                }
            }).start();
        }
    }

    public TreeItem<GitTreeItem> getChanges() {
        return changes;
    }

    public TreeItem<GitTreeItem> getUntracked() {
        return untracked;
    }

}
