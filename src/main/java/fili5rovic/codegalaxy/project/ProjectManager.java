package fili5rovic.codegalaxy.project;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.hierarchy.ProjectHierarchy;
import fili5rovic.codegalaxy.window.Window;

import java.nio.file.Path;

public class ProjectManager {

    private static final DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);

    public static void openProject(Path path) {
        controller.getTreeViewPane().setCenter(new ProjectHierarchy(path.toString()));
    }

}
