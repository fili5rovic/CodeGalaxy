package fili5rovic.codegalaxy.hierarchy;

import fili5rovic.codegalaxy.preferences.UserPreferences;
import fili5rovic.codegalaxy.util.SVG;
import fili5rovic.codegalaxy.util.SVGHelper;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ProjectItem extends TreeItem<Label> {

    private final Path path;

    public ProjectItem(Path path) {
        this.path = path;
        refreshIcon();

        expandedProperty().addListener((_, _, isExpanded) -> {
            if (isExpanded)
                UserPreferences.getInstance().addTo("expanded", path.toString());
            else
                UserPreferences.getInstance().removeFrom("expanded", path.toString());
        });
    }



    public void refreshIcon() {
        Label label = new Label(path.getFileName().toString());
        SVG svgIcon = SVG.FILE;
        double size = label.getFont().getSize();
        if(Files.isDirectory(path)) {
            if (path.toFile().listFiles() == null || Objects.requireNonNull(path.toFile().listFiles()).length == 0)
                svgIcon = SVG.FOLDER_EMPTY;
            else
                svgIcon = SVG.FOLDER;
        }
        else if (path.toString().endsWith(".java"))
            svgIcon = SVG.JAVA_CLASS;
        label.setGraphic(SVGHelper.get(svgIcon, size));
        this.setValue(label);
    }

    public Path getPath() {
        return path;
    }

}
