package fili5rovic.codegalaxy.vcs.treeView;

import javafx.scene.control.ToggleButton;

public class GitTreeItem {
    private ToggleButton button;
    private String name;

    public GitTreeItem(String name) {
        this.name = name;
        this.button = new ToggleButton();
        this.button.getStyleClass().add("git-toggle-button");
    }

    public ToggleButton getToggle() {
        return button;
    }

    public String getName() {
        return name;
    }
}