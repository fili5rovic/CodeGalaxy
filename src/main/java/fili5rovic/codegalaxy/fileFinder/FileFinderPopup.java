package fili5rovic.codegalaxy.fileFinder;

import fili5rovic.codegalaxy.Main;
import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.util.FileHelper;
import fili5rovic.codegalaxy.util.MetaDataHelper;
import fili5rovic.codegalaxy.window.Window;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.stage.Popup;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class FileFinderPopup extends Popup {

    private final ListView<FileItem> listView;

    public FileFinderPopup() {
        super();
        setAutoFix(true);
        setAutoHide(true);
        setHideOnEscape(true);

        this.listView = new ListView<>();

        getScene().getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/main-dark.css")).toExternalForm());

        update();

        listener();
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
    }

    public void update() {
        listView.getItems().clear();

        Path srcDir = Path.of(Objects.requireNonNull(MetaDataHelper.getClasspathPath("src")));
        try {
            for (Path path : FileHelper.getAllFilesInDirectory(srcDir)) {
                listView.getItems().add(new FileItem(path));
            }
        } catch (IOException e) {
            System.err.println("Error reading srcDir: " + e.getMessage());
            return;
        }

        getContent().add(listView);
    }

    @Override
    public void show(javafx.stage.Window owner) {
        super.show(owner);
        requestFocus();
    }
}
