package fili5rovic.codegalaxy.hierarchy;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.util.SVG;
import fili5rovic.codegalaxy.util.SVGHelper;
import fili5rovic.codegalaxy.window.Window;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.nio.file.Files;
import java.nio.file.Path;

public class ProjectItem extends TreeItem<Label> {

    private final Path path;

    private static final DashboardController controller = ((DashboardController) Window.getController(Window.WINDOW_DASHBOARD));

    public ProjectItem(Path path) {
        this.path = path;

        Label label = new Label(path.getFileName().toString());
        double size = label.getFont().getSize();
        if(Files.isDirectory(path))
            label.setGraphic(SVGHelper.get(SVG.FOLDER, size));
        else {
            label.setGraphic(SVGHelper.get(SVG.FILE, size));
        }

        this.setValue(label);
    }

    public Path getPath() {
        return path;
    }

}
