package fili5rovic.codegalaxy.vcs.treeView;

import javafx.scene.control.ToggleButton;

import java.nio.file.Path;

public class GitTreeItem {
    private ToggleButton button;
    private String name;
    private final Path path;

    public GitTreeItem(String name) {
        this.path = Path.of(name);
        this.name = path.getFileName().toString();
        this.button = new ToggleButton();
        this.button.getStyleClass().add("git-toggle-button");
    }

    public ToggleButton getToggle() {
        return button;
    }

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }
}