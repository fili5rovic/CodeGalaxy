package fili5rovic.codegalaxy.codeRunner;

import fili5rovic.codegalaxy.Main;
import fili5rovic.codegalaxy.console.ConsoleArea;
import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.window.Window;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

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
        tab.setContent(new ConsoleArea(process));

        tab.setOnCloseRequest(event -> {
            if (process.isAlive()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Process Termination");
                alert.setHeaderText("The process is still running.");
                alert.setContentText("Are you sure you want to close this tab and terminate the process?");
                alert.setGraphic(null);
                alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/main-dark.css")).toExternalForm());

                ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
                alert.getButtonTypes().setAll(yesButton, noButton);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isEmpty() || result.get() == noButton) {
                    event.consume();
                    return;
                }
            }
            try {
                process.destroy();
            } catch (Exception e) {
                System.err.println("Couldn't destroy process: " + e.getMessage());
            }
            if(controller.getConsoleTabPane().getTabs().size() == 1) {
                controller.getConsoleToggleGroup().selectToggle(null);
                controller.getShowRunToggle().setVisible(false);
            }
        });

        controller.getConsoleTabPane().setVisible(true);
        controller.getConsoleTabPane().getTabs().add(tab);
        controller.getConsoleTabPane().getSelectionModel().select(tab);

        controller.getShowRunToggle().setVisible(true);
        controller.getShowRunToggle().setSelected(true);
    }


}
