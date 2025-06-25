package fili5rovic.codegalaxy.fileFinder;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.util.CSSUtil;
import fili5rovic.codegalaxy.util.FileHelper;
import fili5rovic.codegalaxy.util.MetaDataHelper;
import fili5rovic.codegalaxy.window.Window;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class FileFinderPopup extends Popup {

    private final ListView<FileItem> listView;

    private final TextField searchTextField;

    private Path[] paths;

    public FileFinderPopup() {
        super();
        setAutoFix(true);
        setAutoHide(true);
        setHideOnEscape(true);

        this.listView = new ListView<>();
        this.searchTextField = new TextField();

        CSSUtil.applyStylesheet(getScene().getStylesheets(), "main");

        updateFromDisk();
        structure();

        listener();
    }

    private void structure() {
        VBox vbox = new VBox();

        vbox.getChildren().add(searchTextField);

        listView.setPrefHeight(500);
        listView.setPrefWidth(300);

        update();
        vbox.getChildren().add(listView);

        getContent().add(vbox);
    }


    public void update() {
        listView.getItems().clear();

        for (Path path : paths) {
            if (searchTextField.getText().isEmpty() || path.getFileName().toString().toLowerCase().contains(searchTextField.getText().toLowerCase())) {
                listView.getItems().add(new FileItem(path));
            }
        }
    }

    public void updateFromDisk() {
        try {
            Path srcDir = Path.of(Objects.requireNonNull(MetaDataHelper.getClasspathPath("src")));
            this.paths = FileHelper.getAllFilesInDirectory(srcDir);
        } catch (IOException e) {
            System.err.println("Error reading srcDir: " + e.getMessage());
        }
    }

    @Override
    public void show(javafx.stage.Window owner) {
        super.show(owner);
        searchTextField.requestFocus();
    }

    @Override
    public void hide() {
        super.hide();
        searchTextField.clear();
    }

    private void listener() {
        DashboardController dashboardController = ((DashboardController) Window.getController(Window.WINDOW_DASHBOARD));

        listView.setOnMouseClicked(_ -> {
            FileItem selectedItem = listView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                dashboardController.createTab(selectedItem.getFilePath());
                listView.getSelectionModel().clearSelection();
                hide();
            }
        });

        searchTextField.textProperty().addListener(_ -> update());

        searchTextField.setOnAction(event -> {
            if (listView.getItems().isEmpty())
                return;

            FileItem selectedItem = listView.getItems().getFirst();
            dashboardController.createTab(selectedItem.getFilePath());
            hide();

            event.consume();
        });
    }
}
