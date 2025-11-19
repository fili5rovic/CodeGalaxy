package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.controller.Controllers;
import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.vcs.GitUtil;
import fili5rovic.codegalaxy.projectSettings.ProjectSettingsUtil;
import fili5rovic.codegalaxy.projectSettings.VCSUtil;
import fili5rovic.codegalaxy.util.SVGUtil;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class ToggleManager {

    private static DashboardController controller;

    public static void initialize() {
        controller = Controllers.dashboardController();
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
        controller.getLeftToggleGroup().selectedToggleProperty().addListener((_, _, selected) ->
                SplitPaneManager.showLeftPanel(selected == null));

        controller.getConsoleToggleGroup().selectedToggleProperty().addListener((_, _, selected) ->
                SplitPaneManager.showConsolePanel(selected == null));

        controller.getShowHierarchyToggle().setOnAction(_ -> {
            controller.getGitPane().setVisible(false);
            controller.getTreeViewPane().setVisible(true);
        });

        controller.getLeftToggleGroup().selectedToggleProperty().addListener((_, _, _) ->
                setupLockedDividerLeftGroup());

        controller.getConsoleToggleGroup().selectedToggleProperty().addListener((_, _, _) ->
                setupLockedDividerConsoleGroup());

        controller.getShowGitToggle().setOnAction(_ -> {
            if (!controller.getShowGitToggle().isSelected())
                return;
            controller.getGitPane().setVisible(true);
            controller.getTreeViewPane().setVisible(false);

            if (ProjectSettingsUtil.isVCSInit()) {
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

    public static void setupLockedDividerLeftGroup() {
        Pane first = (Pane) controller.getMainSplitPane().getItems().getFirst();
        if (controller.getLeftToggleGroup().getSelectedToggle() == null) {
            first.maxWidthProperty().set(0);
        } else {
            first.maxWidthProperty().set(Double.MAX_VALUE);
        }
    }

    public static void setupLockedDividerConsoleGroup() {
        Pane last = (Pane) controller.getConsoleSplitPane().getItems().getLast();
        if (controller.getConsoleToggleGroup().getSelectedToggle() == null) {
            last.maxWidthProperty().set(0);
        } else {
            last.maxWidthProperty().set(Double.MAX_VALUE);
        }
    }

    private static void toggleIcons() {
        controller.getShowHierarchyToggle().setGraphic(SVGUtil.getUI("expand", 16));
        controller.getShowGitToggle().setGraphic(SVGUtil.getUI("commit", 16));

        controller.getShowProblemsToggle().setGraphic(SVGUtil.getUI("error", 16));
        controller.getShowRunToggle().setGraphic(SVGUtil.getUI("runBtn", 16));

    }


}
