package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.controller.Controllers;
import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.projectSettings.RunConfigUtil;
import fili5rovic.codegalaxy.projectSettings.dataclass.RunConfiguration;
import javafx.application.Platform;
import javafx.scene.control.ChoiceBox;

import java.io.IOException;

public class ChoiceBoxManager {
    private static DashboardController controller;
    private static ChoiceBox<RunConfiguration> editConfigs;

    public static void initialize() {
        controller = Controllers.dashboardController();
        editConfigs = controller.getEditConfigurationsChoiceBox();
        initConfigs();

        icons();
        actions();
    }

    private static void initConfigs() {
        editConfigs.getItems().clear();
        try {
            editConfigs.getItems().addAll(RunConfigUtil.readRunConfigurations());
        } catch (IOException e) {
            System.err.println("Failed to read run configurations: " + e.getMessage());
        }
        editConfigs.getItems().addLast(new RunConfiguration("New Configuration", "", "Create a new run configuration"));
        editConfigs.getSelectionModel().selectFirst();
    }

    private static void actions() {
        editConfigs.getSelectionModel().selectedItemProperty().addListener((_, prev, selectedConfig) -> {
            if(selectedConfig == null)
                return;

            if(selectedConfig.equals(editConfigs.getItems().getLast())) {
                EditConfigurationsManager.openEditConfigurations();
                editConfigs.getSelectionModel().clearSelection();
                Platform.runLater(() -> editConfigs.getSelectionModel().select(prev));
            }
        });
    }

    private static void icons() {

    }
}
