package fili5rovic.codegalaxy.hierarchy;

import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.util.SVGUtil;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;

import java.nio.file.Path;

public class ProjectItem extends TreeItem<Label> {

    private final Path path;

    private boolean errorFlag = false;

    public ProjectItem(Path path) {
        this.path = path;
        setValue(new Label(path.getFileName().toString()));
        refreshIcon();

        expandedProperty().addListener((_, _, isExpanded) -> {
            Path relativePath = getRelativeProjectPath();

            if (isExpanded)
                IDESettings.getRecentInstance().addTo("expanded", relativePath.toString());
            else
                IDESettings.getRecentInstance().removeFrom("expanded", relativePath.toString());
        });
    }

    public void error(boolean error) {
        this.errorFlag = error;

        if (errorFlag) {
            if (!getValue().getStyleClass().contains("project-item-error"))
                getValue().getStyleClass().add("project-item-error");
        } else
            getValue().getStyleClass().remove("project-item-error");

        getValue().applyCss();

        if (getParent() == null || !(getParent() instanceof ProjectItem parentItem))
            return;

        boolean hasMoreErrors = parentItem.getChildren().stream()
          .anyMatch(child -> child instanceof ProjectItem projectChild && projectChild.errorFlag && !projectChild.equals(this));

        if(!hasMoreErrors)
            parentItem.error(errorFlag);
    }

    public void refreshIcon() {
        Label label = getValue();
        int size = (int) Math.round(label.getFont().getSize());
        size += 4;
        label.setGraphic(SVGUtil.getIconByPath(path, size, 0));

        if (errorFlag) {
            label.getStyleClass().add("project-item-error");
        }

        this.setValue(label);
    }

    public Path getPath() {
        return path;
    }

    public Path getRelativeProjectPath() {
        Path projectPath = Path.of(IDESettings.getRecentInstance().get("lastProjectPath"));
        return projectPath.relativize(path);
    }
}
