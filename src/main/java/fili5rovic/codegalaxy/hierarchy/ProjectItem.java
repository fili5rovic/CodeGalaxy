package fili5rovic.codegalaxy.hierarchy;

import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.util.SVGUtil;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;

import java.nio.file.Path;

public class ProjectItem extends TreeItem<Label> {

    private final Path path;

    private boolean hasError = false;

    public ProjectItem(Path path) {
        this.path = path;
        refreshIcon();

        expandedProperty().addListener((_, _, isExpanded) -> {
            if (isExpanded)
                IDESettings.getInstance().addTo("expanded", path.toString());
            else
                IDESettings.getInstance().removeFrom("expanded", path.toString());
        });
    }

    public void error(boolean error) {
        this.hasError = error;

        if (getValue() != null) {
            if (error)
                getValue().getStyleClass().add("project-item-error");
            else
                getValue().getStyleClass().remove("project-item-error");
            getValue().applyCss();
        }

        if (getParent() instanceof ProjectItem parent) {
            parent.error(error);
        }
    }

    public void refreshIcon() {
        Label label = new Label(path.getFileName().toString());
        int size = (int) Math.round(label.getFont().getSize());
        size += 4;
        label.setGraphic(SVGUtil.getIconByPath(path, size, size, -2));

        if (hasError) {
            label.getStyleClass().add("project-item-error");
        }

        this.setValue(label);
    }

    public Path getPath() {
        return path;
    }

}
