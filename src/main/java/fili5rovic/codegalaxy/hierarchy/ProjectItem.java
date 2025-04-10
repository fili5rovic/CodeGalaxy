package fili5rovic.codegalaxy.hierarchy;

import fili5rovic.codegalaxy.util.SVG;
import fili5rovic.codegalaxy.util.SVGHelper;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProjectItem extends TreeItem<Label> {

    private final Path path;

    public ProjectItem(Path path) {
        this.path = path;
        refreshIcon();
    }

    public void refreshIcon() {
        Label label = new Label(path.getFileName().toString());
        SVG svgIcon = SVG.FILE;
        double size = label.getFont().getSize();
        if(Files.isDirectory(path)) {
            if (path.toFile().listFiles() == null || path.toFile().listFiles().length == 0)
                svgIcon = SVG.FOLDER_EMPTY;
            else
                svgIcon = SVG.FOLDER;
        }
        label.setGraphic(SVGHelper.get(svgIcon, size));
        this.setValue(label);
    }

    public Path getPath() {
        return path;
    }

}
