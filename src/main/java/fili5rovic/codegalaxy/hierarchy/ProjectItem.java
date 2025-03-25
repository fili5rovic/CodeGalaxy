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

        Label label = new Label(path.getFileName().toString());
        double size = label.getFont().getSize();
        if(Files.isDirectory(path))
            label.setGraphic(SVGHelper.get(SVG.FOLDER, size));
        else
            label.setGraphic(SVGHelper.get(SVG.FILE, size));

        this.setValue(label);
    }

    public Path getPath() {
        return path;
    }

}
