package fili5rovic.codegalaxy.hierarchy;

import fili5rovic.codegalaxy.codeRunner.CodeRunnerService;
import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.util.FileHelper;
import fili5rovic.codegalaxy.util.JavaParserUtil;
import fili5rovic.codegalaxy.util.SVGUtil;
import fili5rovic.codegalaxy.window.Window;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class ContextMenuHelper {

    private final Pane filePane;
    private final TextField fileNameTextField;
    private final Label fileNameLabel;

    public ContextMenuHelper() {
        DashboardController controller = ((DashboardController) Window.getController(Window.WINDOW_DASHBOARD));
        filePane = controller.getFilePane();
        fileNameTextField = controller.getFileNameTextField();
        fileNameLabel = controller.getFileNameLabel();

        fileNameTextField.setOnAction(_ -> filePane.setVisible(false));
    }

    public ArrayList<MenuItem> createMenuItems(ArrayList<ProjectItem> items) {
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        ProjectItem firstItem = items.getFirst();

        if (Files.isDirectory(firstItem.getPath()))
            menuItems.add(createNewFile(firstItem));

        if(Files.isRegularFile(firstItem.getPath()) &&
                firstItem.getPath().toString().endsWith(".java") &&
                JavaParserUtil.hasMainMethod(firstItem.getPath().toFile())) {
            MenuItem runItem = new MenuItem("Run");
            runItem.setGraphic(SVGUtil.getEmoji("run",16,16));
            runItem.setOnAction(_ -> {
                CodeRunnerService.runJava(firstItem.getPath());
            });
            menuItems.add(runItem);
            menuItems.add(new SeparatorMenuItem());
        }

        menuItems.add(createCopyPath(items));
        menuItems.add(createOpen(items));
        menuItems.add(createDeleteMenu(items));
        return menuItems;
    }

    private MenuItem createNewFile(ProjectItem item) {
        Menu newItem = new Menu("New");
        newItem.setGraphic(SVGUtil.getEmoji("baby", 16, 16));

        newItem.getItems().addAll(
                createMenuItem("Directory", item, ""),
                createMenuItem("Java Class", item, "java"),
                createMenuItem("Text File", item, "txt")
        );
        return newItem;
    }

    private MenuItem createMenuItem(String name, ProjectItem item, String extension) {
        MenuItem menuItem = new MenuItem(name);
        menuItem.setOnAction(e -> onNewFile(item, extension));
        return menuItem;
    }

    private void onNewFile(ProjectItem item, String extension) {
        filePane.setVisible(true);
        item.setExpanded(true);

        fileNameLabel.setText("New File");

        fileNameTextField.clear();
        fileNameTextField.requestFocus();

        fileNameTextField.setOnAction(e -> {
            try {
                textFieldAction(item, extension);
            } catch (IOException ioException) {
                System.out.println("Couldn't create file");
                filePane.setVisible(false);
            }
        });
    }



    private void textFieldAction(ProjectItem item, String extension) throws IOException {
        String name = fileNameTextField.getText();
        Path path = item.getPath();
        boolean isDir = extension.isEmpty();

        if (isDir) {
            path = path.resolve(name);
            Files.createDirectory(path);
            if (!Files.isDirectory(path))
                throw new FileAlreadyExistsException("Directory already exists");
        } else {
            path = path.resolve(name + '.' + extension);
            Files.createFile(path);
            if (!Files.isRegularFile(path))
                throw new FileAlreadyExistsException("File already exists");
        }
        item.getChildren().add(new ProjectItem(path));
        filePane.setVisible(false);
        item.refreshIcon();
    }

    private MenuItem createDeleteMenu(ArrayList<ProjectItem> items) {
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setGraphic(SVGUtil.getEmoji("trash", 16, 16));
        deleteItem.setOnAction(e -> {
            items.forEach(item -> {
                try {
                    FileHelper.deleteRecursively(item.getPath());
                    ((ProjectItem) item.getParent()).refreshIcon();
                    item.getParent().getChildren().remove(item);
                } catch (IOException ioException) {
                    System.out.println("Couldn't delete item");
                }
            });
        });
        return deleteItem;
    }

    private MenuItem createOpen(ArrayList<ProjectItem> items) {
        MenuItem openItem = new MenuItem("Open Folder");
        openItem.setGraphic(SVGUtil.getEmoji("look", 16, 16));
        openItem.setOnAction(e -> {
            items.forEach(item -> {
                try {
                    Path path = item.getPath();
                    if (!Files.isDirectory(item.getPath()))
                        path = path.getParent();
                    FileHelper.openDirectoryInExplorer(path.toFile());
                } catch (IOException ioException) {
                    System.err.println("Couldn't open folder");
                }
            });
        });
        return openItem;
    }

    private MenuItem createCopyPath(ArrayList<ProjectItem> items) {
        MenuItem copyPathItem = new MenuItem("Copy Path");
        copyPathItem.setGraphic(SVGUtil.getEmoji("copy", 16, 16));
        copyPathItem.setOnAction(e -> {
            items.forEach(item -> {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(item.getPath().toString());
                clipboard.setContent(content);
            });
        });
        return copyPathItem;
    }



}
