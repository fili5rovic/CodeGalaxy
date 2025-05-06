package fili5rovic.codegalaxy.codeRunner;

import fili5rovic.codegalaxy.console.ConsoleArea;
import fili5rovic.codegalaxy.console.Redirector;
import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.window.Window;
import javafx.scene.control.Tab;

import java.nio.file.Path;

public class CodeRunnerService {

    private static final DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);

    public static void runJava(Path javaFilePath) {
        try {
            Process process = CodeRunner.runJava(javaFilePath);

            addTab(javaFilePath, process);
        } catch (Exception e) {
            System.err.println("Couldn't run file: " + e.getMessage());
        }
    }

    private static void addTab(Path javaFilePath, Process process) {
        String title = javaFilePath.getFileName().toString();
        Tab tab = new Tab(title);
        tab.setTooltip(new javafx.scene.control.Tooltip(javaFilePath.toString()));
        tab.setContent(new ConsoleArea(process));

        tab.setOnCloseRequest(_ -> {
            try {
                process.destroy();
            } catch (Exception e) {
                System.err.println("Couldn't destroy process: " + e.getMessage());
            }
        });

        controller.getConsoleTabPane().getTabs().add(tab);
        controller.getConsoleTabPane().getSelectionModel().select(tab);

    }


}
