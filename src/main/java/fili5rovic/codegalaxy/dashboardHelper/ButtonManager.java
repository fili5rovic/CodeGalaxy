package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.util.FileHelper;
import fili5rovic.codegalaxy.vcs.GitUtil;
import fili5rovic.codegalaxy.window.Window;
import javafx.stage.Stage;

import java.io.File;

public class ButtonManager {
    private static final DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);

    public static void initialize() {
        buttonIcons();
        buttonActions();
    }

    private static void buttonActions() {
        controller.getInitRepoBtn().setOnAction(_ -> {
            initRepoBtn();
        });

        controller.getCommitBtn().setOnAction(_ -> {
            GitUtil.instance().updateHierarchy();
        });
    }

    private static void initRepoBtn() {
        File projectDirectory = new File(IDESettings.getInstance().get("lastProjectPath"));
        Stage stage = Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage();
        File repoFile = FileHelper.openFolderChooser(stage, projectDirectory);

        GitUtil.instance().init(repoFile.getAbsolutePath());

//        controller.getGitInitPane().setVisible(false);
//        controller.getGitBorderPane().setVisible(true);
    }

    private static void buttonIcons() {

    }
}
