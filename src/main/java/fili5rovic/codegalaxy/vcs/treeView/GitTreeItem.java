package fili5rovic.codegalaxy.vcs.treeView;

import javafx.scene.control.ToggleButton;

public class GitTreeItem {
    private ToggleButton button;
    private String name;

    public GitTreeItem(String name) {
        this.name = name;
        this.button = new ToggleButton();
        this.button.getStyleClass().add("git-toggle-button");
        this.button.setOnAction(event -> {
            System.out.println("Toggle button clicked: " + name);
        });
    }

    public ToggleButton getValue() {
        return button;
    }

    public String getName() {
        return name;
    }
}