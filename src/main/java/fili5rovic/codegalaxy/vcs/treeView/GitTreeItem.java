package fili5rovic.codegalaxy.vcs.treeView;

import javafx.scene.control.ToggleButton;

import java.nio.file.Path;

public class GitTreeItem {
    private final ToggleButton button;
    private final String name;
    private final Path path;

    private boolean modified = false;

    public GitTreeItem(String name) {
        this.path = Path.of(name);
        this.name = path.getFileName().toString();
        this.button = new ToggleButton();
        this.button.getStyleClass().add("git-toggle-button");
    }

    public GitTreeItem modified() {
        this.modified = true;
        return this;
    }

    public boolean isModified() {
        return modified;
    }

    public ToggleButton getToggle() {
        return button;
    }

    public String getName() {
        return name;
    }

    public String getPathGit() {
        return path.toString().replace("\\", "/");
    }
}