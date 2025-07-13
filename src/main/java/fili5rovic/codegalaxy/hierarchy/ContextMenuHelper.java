package fili5rovic.codegalaxy.hierarchy;

import fili5rovic.codegalaxy.codeRunner.CodeRunnerService;
import fili5rovic.codegalaxy.controller.Controllers;
import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.dashboardHelper.ProjectManager;
import fili5rovic.codegalaxy.hierarchy.newfilepopup.listview.ItemEntry;
import fili5rovic.codegalaxy.hierarchy.newfilepopup.listview.SuggestedJavaTypeListView;
import fili5rovic.codegalaxy.util.FileHelper;
import fili5rovic.codegalaxy.util.JavaParserUtil;
import fili5rovic.codegalaxy.util.SVGUtil;
import fili5rovic.codegalaxy.window.Window;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class ContextMenuHelper {

    private final TextField fileNameTextField;

    private final Popup filePanePopup = new Popup();

    private final SuggestedJavaTypeListView listView;

    private final DashboardController controller;

    public ContextMenuHelper() {
        controller = Controllers.dashboardController();
        fileNameTextField = new TextField();
        VBox vbox = new VBox(fileNameTextField);

        listView = new SuggestedJavaTypeListView();

        vbox.getChildren().add(listView);
        filePanePopup.getContent().add(vbox);

        filePanePopup.setAutoHide(true);
        fileNameTextField.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode().equals(KeyCode.UP) || e.getCode().equals(KeyCode.DOWN)) {
                listView.fireEvent(e.copyFor(listView, listView));
                e.consume();
            }
        });


    }

    public ArrayList<MenuItem> createMenuItems(ArrayList<ProjectItem> items) {
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        ProjectItem firstItem = items.getFirst();

        if (Files.isDirectory(firstItem.getPath())) menuItems.add(createNewFile(firstItem));

        if (Files.isRegularFile(firstItem.getPath()) && firstItem.getPath().toString().endsWith(".java") && JavaParserUtil.hasMainMethod(firstItem.getPath().toFile())) {
            MenuItem runItem = new MenuItem("Run");
            runItem.setGraphic(SVGUtil.getEmoji("run", 16, 16));
            runItem.setOnAction(_ -> {
                CodeRunnerService.runJava(firstItem.getPath());
                ((DashboardController) Window.getController(Window.WINDOW_DASHBOARD)).getErrorTabPane().setVisible(false);
            });
            menuItems.add(runItem);
            menuItems.add(new SeparatorMenuItem());
        }

        menuItems.add(createCopyPath(items));
        menuItems.add(createOpen(items));
        menuItems.add(createDeleteMenu(items));
        menuItems.add(new SeparatorMenuItem());
        menuItems.add(refreshItem());

        return menuItems;
    }

    private MenuItem createNewFile(ProjectItem item) {
        Menu newItem = new Menu("New");
        newItem.setGraphic(SVGUtil.getEmoji("baby", 16, 16));

        newItem.getItems().addAll(createMenuItem("Directory", item, ""), createMenuItem("Java Class", item, "java"), createMenuItem("Text File", item, "txt"));
        return newItem;
    }

    private MenuItem createMenuItem(String name, ProjectItem item, String extension) {
        MenuItem menuItem = new MenuItem(name);
        menuItem.setOnAction(_ -> onNewFile(item, extension));
        return menuItem;
    }

    private void onNewFile(ProjectItem item, String extension) {
        filePanePopup.show(controller.getOpenCodeGalaxy().getScene().getWindow());
        item.setExpanded(true);

        fileNameTextField.clear();
        fileNameTextField.requestFocus();

        fileNameTextField.setOnAction(_ -> {
            try {
                createJavaFileAction(item, extension);
            } catch (IOException e) {
                System.err.println("Couldn't create file");
                System.err.println(e.getMessage());
            }
            filePanePopup.hide();
        });

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY && !fileNameTextField.getText().isBlank()) {
                ItemEntry selectedItem = listView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    try {
                        createJavaFileAction(item, extension);
                    } catch (IOException e) {
                        System.err.println("Couldn't create file");
                        System.err.println(e.getMessage());
                    }
                    filePanePopup.hide();
                }
            }
        });
    }

    private void createJavaFileAction(ProjectItem item, String extension) throws IOException {
        String name = fileNameTextField.getText();
        Path path = item.getPath();
        boolean isDir = extension.isEmpty();

        if (isDir) {
            path = path.resolve(name);
            Files.createDirectory(path);
            if (!Files.isDirectory(path)) throw new FileAlreadyExistsException("Directory already exists");
        } else {
            path = path.resolve(name + '.' + extension);
            Files.createFile(path);
            if (!Files.isRegularFile(path)) throw new FileAlreadyExistsException("File already exists");
            if (extension.equals("java")) {
                String content = getContentForNewFile(name);
                Files.writeString(path, content);
            }

            controller.createTab(path);
        }
        ProjectManager.reloadHierarchy(item);
        filePanePopup.hide();
    }

    private String getContentForNewFile(String name) {
        ItemEntry selectedRow = listView.getSelectionModel().getSelectedItem();
        String type = selectedRow.label().toLowerCase();
        if(type.equals("exception")) {
            return """
                    public class %s extends RuntimeException {
                      public %s(String message) {
                        super(message);
                      }
                    }
                    """.formatted(name, name);
        }
        if(type.equals("annotation")) {
            type = "@interface";
        }
        return """
                 public %s %s {
                    \s
                 }
                \s""".formatted(type, name);
    }

    private MenuItem createDeleteMenu(ArrayList<ProjectItem> items) {
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setGraphic(SVGUtil.getEmoji("trash", 16, 16));
        deleteItem.setOnAction(_ -> {
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
        openItem.setOnAction(_ -> {
            items.forEach(item -> {
                try {
                    Path path = item.getPath();
                    if (!Files.isDirectory(item.getPath())) path = path.getParent();
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
        copyPathItem.setOnAction(_ -> {
            items.forEach(item -> {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(item.getPath().toString());
                clipboard.setContent(content);
            });
        });
        return copyPathItem;
    }

    private MenuItem refreshItem() {
        MenuItem refreshItem = new MenuItem("Refresh");
        refreshItem.setGraphic(SVGUtil.getEmoji("refresh", 16, 16));
        refreshItem.setOnAction(_ -> ProjectManager.reloadHierarchy());
        return refreshItem;
    }

}
