package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.codeRunner.CodeRunnerJava;
import fili5rovic.codegalaxy.projectSettings.RunConfigUtil;
import fili5rovic.codegalaxy.projectSettings.dataclass.RunConfiguration;
import fili5rovic.codegalaxy.util.MetaDataHelper;
import fili5rovic.codegalaxy.util.SVGUtil;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

public class EditConfigurationsManager {

    private static BorderPane configurationsPane;

    private static final List<RunConfiguration> configurations = new ArrayList<>();

    public static void openEditConfigurations() {
        TabManager.createTab(createConfigurationsPane(), "Configurations");
    }

    private static ListView<RunConfiguration> getConfigList() {
        ListView<RunConfiguration> configList = new ListView<>();
        configList.getItems().addAll(configurations);
        return configList;
    }

    public static BorderPane createConfigurationsPane() {
        BorderPane configurationsPane = new BorderPane();
        configurationsPane.getStyleClass().add("dark-background");

        ObservableList<RunConfiguration> configItems = FXCollections.observableArrayList(configurations);

        TableView<RunConfiguration> configTable = new TableView<>(configItems);
        configTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Only name column now
        TableColumn<RunConfiguration, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getConfigName()));

        configTable.getColumns().add(nameColumn);

        VBox editorPane = new VBox(10);
        editorPane.setPadding(new Insets(20));
        Label noSelection = new Label("Select a configuration to edit.");
        editorPane.getChildren().add(noSelection);

        // Delete button - initially disabled
        Button deleteButton = new Button("Delete");
        deleteButton.setDisable(true);

        configTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, conf) -> {
            // Enable/disable delete button based on selection
            deleteButton.setDisable(conf == null);

            editorPane.getChildren().clear();
            if (conf == null) {
                editorPane.getChildren().add(noSelection);
                return;
            }

            TextField nameField = new TextField(conf.getConfigName());
            TextField argsField = new TextField();
            if (conf.getProgramArgs() != null && conf.getProgramArgs().length > 0) {
                argsField.setText(String.join(" ", conf.getProgramArgs()));
            }

            TextField fullNameField = new TextField(conf.getFullName());
            Button chooseFileButton = new Button();
            chooseFileButton.setGraphic(SVGUtil.getEmoji("look", 16));

            HBox fullNameBox = new HBox(5, chooseFileButton, fullNameField);
            chooseFileButton.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select Java File");
                fileChooser.setInitialDirectory(new File(MetaDataHelper.getSrcPath()));
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java Files", "*.java"));
                File file = fileChooser.showOpenDialog(configurationsPane.getScene().getWindow());
                if (file != null) {
                    String qualifiedClassName = CodeRunnerJava.getQualifiedClassName(Path.of(file.getAbsolutePath()));
                    fullNameField.setText(qualifiedClassName != null ? qualifiedClassName : "");
                }
            });

            Button saveButton = new Button("Save");
            saveButton.setOnAction(e -> {
                conf.setConfigName(nameField.getText());
                conf.setFullName(fullNameField.getText());
                String argsText = argsField.getText().trim();
                conf.setProgramArgs(argsText.isEmpty() ? new String[0] : argsText.split("\\s+"));

                configTable.refresh();

                configurations.clear();
                configurations.addAll(configItems);
                ChoiceBoxManager.updateEditConfigs();
            });

            editorPane.getChildren().addAll(
                    new Label("Name:"), nameField,
                    new Label("Program Arguments:"), argsField,
                    new Label("Full Name:"), fullNameBox,
                    saveButton
            );
        });

        // Delete button action
        deleteButton.setOnAction(e -> {
            RunConfiguration selected = configTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                configItems.remove(selected);
                configurations.clear();
                configurations.addAll(configItems);
                ChoiceBoxManager.updateEditConfigs();
            }
        });

        Button addButton = new Button("Add New");
        addButton.setOnAction(e -> {
            RunConfiguration newConf = new RunConfiguration("New Config", "", new String[0], new String[0]);
            configItems.add(newConf);
            configTable.getSelectionModel().select(newConf);
        });

        // Button layout with both Add and Delete buttons
        HBox buttonBox = new HBox(10, addButton, deleteButton);
        VBox leftBox = new VBox(10, configTable, buttonBox);
        leftBox.setPadding(new Insets(10));

        configurationsPane.setLeft(leftBox);
        configurationsPane.setCenter(editorPane);

        if (!configItems.isEmpty()) {
            configTable.getSelectionModel().selectFirst();
        }

        return configurationsPane;
    }

    public static List<RunConfiguration> getConfigurations() {
        return configurations;
    }
}