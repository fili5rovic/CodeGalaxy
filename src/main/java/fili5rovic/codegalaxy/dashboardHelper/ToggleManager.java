package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.vcs.GitUtil;
import fili5rovic.codegalaxy.projectSetings.ProjectSettingsUtil;
import fili5rovic.codegalaxy.projectSetings.VCSUtil;
import fili5rovic.codegalaxy.util.SVGUtil;
import fili5rovic.codegalaxy.window.Window;
import javafx.scene.control.SplitPane;

import java.io.IOException;

public class ToggleManager {

    private static final DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);

    private static double lastConsoleDividerValue = 0.7;

    public static void initialize() {
        toggleIcons();
        toggleActions();

        controller.getGitPane().setVisible(false);
        controller.getGitBorderPane().setVisible(false);
        controller.getGitInitPane().setVisible(false);
    }

    public static void refreshIcons() {
        toggleIcons();
    }

    private static void toggleActions() {
        controller.getLeftToggleGroup().selectedToggleProperty().addListener((_, _, selected) -> SplitPaneManager.showLeftPanel(selected == null));

        controller.getShowHierarchyToggle().setOnAction(_ -> {
            controller.getGitPane().setVisible(false);
            controller.getTreeViewPane().setVisible(true);
        });

        controller.getShowGitToggle().setOnAction(_ -> {
            if(!controller.getShowGitToggle().isSelected())
                return;
            controller.getGitPane().setVisible(true);
            controller.getTreeViewPane().setVisible(false);

            // todo search for .git when folder is opened and VCS is not set
            if(ProjectSettingsUtil.isVCSInit()) {
                try {
                    GitUtil.instance().open(VCSUtil.readVcsSettings().getRepositoryPath());
                } catch (IOException e) {
                    System.err.println("Failed to open VCS repository: " + e.getMessage());
                }
                GitUtil.instance().updateHierarchy();
                controller.getGitBorderPane().setVisible(true);
                controller.getGitInitPane().setVisible(false);
            } else {
                controller.getGitBorderPane().setVisible(false);
                controller.getGitInitPane().setVisible(true);
            }

        });

        controller.getConsoleToggleGroup().selectedToggleProperty().addListener((_, oldToggle, newToggle) -> {
            SplitPane.Divider divider = controller.getConsoleSplitPane().getDividers().getFirst();
            if (newToggle == null && oldToggle != null) {
                lastConsoleDividerValue = divider.getPosition();
                divider.setPosition(1);
            } else if (newToggle != null && oldToggle == null) {
                divider.setPosition(lastConsoleDividerValue);
            }
        });

        controller.getShowProblemsToggle().setOnAction(_ -> {
            if (controller.getShowProblemsToggle().isSelected()) {
                controller.getConsoleTabPane().setVisible(false);
                controller.getErrorTabPane().setVisible(true);
            } else {
                controller.getErrorTabPane().setVisible(false);
            }
        });

        controller.getShowRunToggle().setOnAction(_ -> {
            if (controller.getShowRunToggle().isSelected()) {
                controller.getConsoleTabPane().setVisible(true);
                controller.getErrorTabPane().setVisible(false);
            } else {
                controller.getConsoleTabPane().setVisible(false);
            }
        });


    }

    private static void toggleIcons() {
        controller.getShowHierarchyToggle().setGraphic(SVGUtil.getUI("expand", 16, 16));
        controller.getShowGitToggle().setGraphic(SVGUtil.getUI("commit", 16, 16));

        controller.getShowProblemsToggle().setGraphic(SVGUtil.getUI("error", 16, 16));
        controller.getShowRunToggle().setGraphic(SVGUtil.getUI("runBtn", 16, 16));

    }


}
