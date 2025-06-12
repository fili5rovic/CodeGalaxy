package fili5rovic.codegalaxy.vcs.treeView;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;

public class GitTreeItem extends TreeItem<ToggleButton> {

    public GitTreeItem(String name) {
        super(new ToggleButton(name));
        getValue().setOnAction(event -> {
            System.out.println("Toggle button clicked: " + name);
        });
    }
}
