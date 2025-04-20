package fili5rovic.codegalaxy.hierarchy;

import fili5rovic.codegalaxy.preferences.UserPreferences;
import fili5rovic.codegalaxy.util.SVGUtil;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import java.nio.file.Path;

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
        label.setGraphic(SVGUtil.getIconByPath(path, 16, 16, -2));
        this.setValue(label);
    }

    public Path getPath() {
        return path;
    }

}
