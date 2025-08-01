package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.controller.Controllers;
import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.projectSettings.dataclass.RunConfiguration;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;

import java.util.ArrayList;
import java.util.List;

public class ChoiceBoxManager {
    private static DashboardController controller;
    private static ChoiceBox<RunConfiguration> editConfigs;

    public static void initialize() {
        controller = Controllers.dashboardController();
        editConfigs = controller.getEditConfigurationsChoiceBox();
        updateEditConfigs();

        icons();
        actions();
    }



    public static void updateEditConfigs() {
        List<RunConfiguration> configurations = EditConfigurationsManager.getConfigurations();
        RunConfiguration selected = editConfigs.getSelectionModel().getSelectedItem();

        List<RunConfiguration> updatedList = new ArrayList<>(configurations);
        updatedList.add(RunConfiguration.EDIT);

        editConfigs.getItems().clear();
        editConfigs.setItems(FXCollections.observableArrayList(updatedList));

        editConfigs.hide();

        if (selected != null && updatedList.contains(selected)) {
            Platform.runLater(() -> editConfigs.getSelectionModel().select(selected));
        }
    }


    private static void actions() {
        editConfigs.getSelectionModel().selectedItemProperty().addListener((_, prev, selectedConfig) -> {
            if(selectedConfig == null)
                return;

            if(selectedConfig.equals(RunConfiguration.EDIT)) {
                EditConfigurationsManager.openEditConfigurations();
                editConfigs.getSelectionModel().clearSelection();
                if (prev != null && !prev.equals(RunConfiguration.EDIT)) {
                    Platform.runLater(() -> editConfigs.getSelectionModel().select(prev));
                }
            }
        });
    }

    private static void icons() {

    }
}
