package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.controller.Controllers;
import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.notification.NotificationManager;
import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.util.FileHelper;
import fili5rovic.codegalaxy.vcs.GitUtil;
import fili5rovic.codegalaxy.vcs.treeView.GitHierarchy;
import fili5rovic.codegalaxy.window.Window;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashSet;

public class ButtonManager {
    private static DashboardController controller;

    public static void initialize() {
        controller = Controllers.dashboardController();
        buttonIcons();
        buttonActions();
    }

    private static void buttonActions() {
        controller.getInitRepoBtn().setOnAction(_ -> {
            initRepoBtn();
        });

        controller.getCommitBtn().setOnAction(_ -> {
            String commitMsg = controller.getCommitMsg().getText();

            GitHierarchy hierarchy = (GitHierarchy) controller.getGitTreeViewPane().getCenter();

            HashSet<String> restoredFiles = new HashSet<>();

            hierarchy.getChanges().getChildren().forEach(change -> {
                System.out.println("Processing change: " + change.getValue().getPathGit() + " - Selected: " + change.getValue().getToggle().isSelected());
                if (!change.getValue().getToggle().isSelected()) {
                    System.out.println("Restoring file: " + change.getValue().getPathGit());
                    String fileString = change.getValue().getPathGit();
                    GitUtil.instance().restore(fileString);
                    restoredFiles.add(fileString);
                } else if(change.getValue().isModified()) {
                    System.out.println("Adding modified file: " + change.getValue().getPathGit());
                    GitUtil.instance().add(change.getValue().getPathGit());
                }
            });

            hierarchy.getUntracked().getChildren().forEach(untracked -> {
                if (untracked.getValue().getToggle().isSelected()) {
                    GitUtil.instance().add(untracked.getValue().getPathGit());
                    System.out.println("Added untracked file: " + untracked.getValue().getPathGit());
                }
            });

            System.out.println("Committing changes with message: " + commitMsg);
            GitUtil.instance().commit(commitMsg);

            restoredFiles.forEach(file -> GitUtil.instance().add(file));

            GitUtil.instance().updateHierarchy();

            int fileCount = 0;

            try {
                fileCount = GitUtil.instance().getFileCountInLastCommit();
            } catch (Exception e) {
                System.err.println("Couldn't get file count: " + e.getMessage());
            }

            NotificationManager.show("Commit successful",fileCount + " files commited successful");
        });
    }

    private static void initRepoBtn() {
        File projectDirectory = new File(IDESettings.getInstance().get("lastProjectPath"));
        Stage stage = Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage();
        File repoFile = FileHelper.openFolderChooser(stage, projectDirectory);

        GitUtil.instance().init(repoFile.getAbsolutePath());

        controller.getGitInitPane().setVisible(false);
        controller.getGitBorderPane().setVisible(true);

        GitUtil.instance().updateHierarchy();

    }

    private static void buttonIcons() {

    }
}
