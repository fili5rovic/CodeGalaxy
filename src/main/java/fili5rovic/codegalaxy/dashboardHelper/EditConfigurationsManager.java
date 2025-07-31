package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.projectSettings.RunConfigUtil;
import fili5rovic.codegalaxy.projectSettings.dataclass.RunConfiguration;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class EditConfigurationsManager {

    private static BorderPane configurationsPane;

    public static void openEditConfigurations() {
        if (configurationsPane == null) {
            configurationsPane = createConfigurationsPane();
        }

        TabManager.createTab(configurationsPane, "Configurations");
    }

    private static ListView<RunConfiguration> getSavedConfigList() {
        ListView<RunConfiguration> configList = new ListView<>();
        RunConfiguration[] data = new RunConfiguration[0];
        try {
            data = RunConfigUtil.readRunConfigurations();
        } catch (IOException e) {
            System.err.println("Failed to read run configurations: " + e.getMessage());
        }
        configList.getItems().addAll(data);
        return configList;
    }

    public static BorderPane createConfigurationsPane() {
        BorderPane configurationsPane = new BorderPane();
        configurationsPane.getStyleClass().add("dark-background");

        ListView<RunConfiguration> configList = getSavedConfigList();

        VBox editorPane = new VBox(10);
        editorPane.setPadding(new Insets(20));
        Label noSelection = new Label("Select a configuration to edit.");
        editorPane.getChildren().add(noSelection);

        configList.getSelectionModel().selectedItemProperty().addListener((_, _, conf) -> {
            editorPane.getChildren().clear();
            if (conf == null) {
                editorPane.getChildren().add(noSelection);
                return;
            }

            TextField nameField = new TextField(conf.getConfigName());
            TextField argsField = new TextField(conf.getProgramArgs());
            TextField fullNameField = new TextField(conf.getFullName());

            Button saveButton = new Button("Save");
            saveButton.setOnAction(e -> {
                conf.setConfigName(nameField.getText());
                conf.setProgramArgs(argsField.getText());
                conf.setFullName(fullNameField.getText());
                configList.refresh();
                try {
                    RunConfigUtil.writeRunConfigurations(configList.getItems().toArray(new RunConfiguration[0]));
                } catch (IOException ex) {
                    System.err.println("Failed to save run configurations: " + ex.getMessage());
                }
            });


            editorPane.getChildren().addAll(
                    new Label("Name:"), nameField,
                    new Label("Program Arguments:"), argsField,
                    new Label("Full Name:"), fullNameField,
                    saveButton
            );


        });

        configurationsPane.setLeft(configList);
        configurationsPane.setCenter(editorPane);

        if (!configList.getItems().isEmpty()) {
            configList.getSelectionModel().selectFirst();
        }

        return configurationsPane;
    }
}
