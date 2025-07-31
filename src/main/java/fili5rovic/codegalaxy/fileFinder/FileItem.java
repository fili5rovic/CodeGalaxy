package fili5rovic.codegalaxy.fileFinder;

import fili5rovic.codegalaxy.util.SVGUtil;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.nio.file.Path;

public class FileItem {
    private final String fileName;
    private final Path filePath;

    public FileItem(Path path) {
        this.fileName = path.getFileName().toString();
        this.filePath = path;
    }

    public Path getFilePath() { return filePath; }
    @Override
    public String toString() { return fileName; }

    public static Callback<ListView<FileItem>, ListCell<FileItem>> createCellFactory() {
        return _ -> new ListCell<>() {
            @Override
            protected void updateItem(FileItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    ImageView icon = SVGUtil.getIconByPath(item.getFilePath(), 16, 0);
                    icon.setMouseTransparent(true);
                    setGraphic(icon);
                    setText(item.getFilePath().getFileName().toString());
                }
            }
        };
    }
}

