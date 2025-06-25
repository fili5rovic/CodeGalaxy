package fili5rovic.codegalaxy.vcs;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.vcs.treeView.GitHierarchy;
import fili5rovic.codegalaxy.vcs.treeView.GitTreeItem;
import fili5rovic.codegalaxy.window.Window;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;

public class GitListenerUtil {

    private static final DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);

    public static void applyGitCommitTextListener(TreeItem<GitTreeItem> root) {
        TextArea textArea = controller.getCommitMsg();

        textArea.textProperty().addListener((_, _, newValue) -> {
            boolean hasCommitMessage = newValue != null && !newValue.isEmpty();
            toggleListener(hasCommitMessage, root);
        });
    }



    public static void toggleListener(boolean selected, TreeItem<GitTreeItem> root) {
        boolean commitMsgEmpty = controller.getCommitMsg().getText().isEmpty();
        if(commitMsgEmpty) {
            controller.getCommitBtn().setDisable(true);
            return;
        }
        if(selected) {
            controller.getCommitBtn().setDisable(false);
            return;
        }

        boolean shouldDisable = !findSelected(root);
        controller.getCommitBtn().setDisable(shouldDisable);
    }

    private static boolean findSelected(TreeItem<GitTreeItem> item) {
        if (item.getValue().getToggle().isSelected()) {
            return true;
        }

        for (TreeItem<GitTreeItem> child : item.getChildren()) {
            if (findSelected(child)) {
                return true;
            }
        }

        return false;
    }
}
