package fili5rovic.codegalaxy.code.manager.codeActions.rightClick;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.codeRunner.CodeRunnerService;
import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.lsp.LSP;
import fili5rovic.codegalaxy.util.JavaParserUtil;
import fili5rovic.codegalaxy.util.SVGUtil;
import fili5rovic.codegalaxy.window.Window;
import javafx.application.Platform;
import javafx.scene.control.*;
import org.eclipse.lsp4j.Location;
import org.fxmisc.richtext.model.TwoDimensional;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CodeRightClickManager extends Manager {

    private final ContextMenu contextMenu = new ContextMenu();

    private final TextFieldLabelPopup renamePopup = new TextFieldLabelPopup();

    private final LocationsPopup locationsPopup = new LocationsPopup();

    public CodeRightClickManager(CodeGalaxy cg) {
        super(cg);

        contextMenu.setAutoHide(true);
        contextMenu.setStyle("-fx-font-size: 18px"); // hardcoded for now
    }

    @Override
    public void init() {
        codeGalaxy.setOnContextMenuRequested(e -> {
            int position = codeGalaxy.hit(e.getX(), e.getY()).getInsertionIndex();
            codeGalaxy.displaceCaret(position);
            codeGalaxy.moveTo(position);

            if (!codeGalaxy.hasSelection())
                codeGalaxy.selectWordAtCaret();

            updateContextMenuItems();

            codeGalaxy.setContextMenu(contextMenu);
            contextMenu.show(codeGalaxy, e.getScreenX(), e.getScreenY());
        });

        renamePopup.hide();
    }

    private void updateContextMenuItems() {
        contextMenu.getItems().clear();

        // TODO Should check for Run after changing file content, right now it only works at the start
        if (codeGalaxy.getFilePath() != null && codeGalaxy.getFilePath().toString().endsWith(".java")) {
            if (JavaParserUtil.hasMainMethod(codeGalaxy.getFilePath().toFile())) {
                MenuItem run = new MenuItem("Run");
                run.setGraphic(SVGUtil.getEmoji("run", 16, 16));
                run.setOnAction(e -> {
                    CodeRunnerService.runJava(codeGalaxy.getFilePath());
                });
                contextMenu.getItems().add(run);
            }
        }

        contextMenu.getItems().add(createGotoMenuItems());

        MenuItem copy = new MenuItem("Copy");
        copy.setGraphic(SVGUtil.getUI("copy", 16, 16));
        copy.setOnAction(e -> {
            codeGalaxy.copy();
        });
        contextMenu.getItems().add(copy);

        MenuItem paste = new MenuItem("Paste");
        paste.setGraphic(SVGUtil.getUI("paste", 16, 16));
        paste.setOnAction(e -> {
            codeGalaxy.paste();
        });
        contextMenu.getItems().add(paste);

        MenuItem cut = new MenuItem("Cut");
        cut.setGraphic(SVGUtil.getUI("cut", 16, 16));
        cut.setOnAction(e -> {
            codeGalaxy.cut();
        });
        contextMenu.getItems().add(cut);

        contextMenu.getItems().add(new SeparatorMenuItem());

        Menu refactor = new Menu("Refactor");
        refactor.getItems().add(createRenameMenuItem());
        contextMenu.getItems().add(refactor);

        contextMenu.getItems().add(new SeparatorMenuItem());

        MenuItem format = new MenuItem("Format Code");
        format.setGraphic(SVGUtil.getUI("format", 16, 16));
        format.setOnAction(e -> {
            codeGalaxy.format();
        });
        contextMenu.getItems().add(format);
    }

    private Menu createGotoMenuItems() {
        Menu gotoMenu = new Menu("Go to");
        MenuItem definition = new MenuItem("Definition");
        definition.setOnAction(e -> {
            codeGalaxy.selectWordAtCaret();
            int line = codeGalaxy.getCurrentParagraph();
            int character = codeGalaxy.getCaretColumn();
            try {
                List<? extends Location> locations = LSP.instance().goToDefinition(codeGalaxy.getFilePath().toString(), line, character).get();
                displayLocations(locations);
            } catch (InterruptedException ex) {
                System.out.println("Go to definition interrupted: " + ex.getMessage());
            } catch (ExecutionException ex) {
                System.err.println("Failed to get definition: " + ex.getMessage());
            }
        });
        gotoMenu.getItems().add(definition);

        MenuItem references = new MenuItem("References");
        references.setOnAction(e -> {
            codeGalaxy.selectWordAtCaret();
            int line = codeGalaxy.getCurrentParagraph();
            int character = codeGalaxy.getCaretColumn();
            try {
                List<? extends Location> locations = LSP.instance().references(codeGalaxy.getFilePath().toString(), line, character).get();
                displayLocations(locations);
            } catch (InterruptedException ex) {
                System.out.println("Go to definition interrupted: " + ex.getMessage());
            } catch (ExecutionException ex) {
                System.err.println("Failed to get definition: " + ex.getMessage());
            }
        });
        gotoMenu.getItems().add(references);
        return gotoMenu;
    }

    private void displayLocations(List<? extends Location> list) {
        if (list.isEmpty())
            return;

        if (list.size() == 1) {
            findInCodeGalaxy(list.getFirst());
        } else {
            // TODO: Test multiple locations working
            System.out.println("Multiple locations found: " + list.size());

            locationsPopup.setLocations(list);
            locationsPopup.setOnLocationSelected(this::findInCodeGalaxy);
            locationsPopup.updateContent();
            locationsPopup.show(codeGalaxy.getScene().getWindow());
        }

    }

    private void findInCodeGalaxy(Location location) {
        DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);

        String uri = location.getUri();
        int defLine = location.getRange().getStart().getLine();
        int defCharacter = location.getRange().getStart().getCharacter();
        if (uri.equals(codeGalaxy.getFilePath().toUri().toString())) {
            Platform.runLater(() -> codeGalaxy.moveTo(defLine, defCharacter));
        } else {
            Platform.runLater(() -> {
                try {
                    // Convert URI to Path properly
                    Path filePath = Paths.get(URI.create(uri));
                    controller.createTab(filePath);
                    Tab tab = controller.getTabPane().getSelectionModel().getSelectedItem();
                    CodeGalaxy content = (CodeGalaxy) tab.getContent();
                    content.requestFocus();
                    content.moveTo(defLine, defCharacter);
                } catch (Exception ex) {
                    System.err.println("Failed to open file: " + uri);
                    ex.printStackTrace();
                }
            });
        }
    }

    private MenuItem createRenameMenuItem() {
        MenuItem rename = new MenuItem("Rename");
        rename.setOnAction(e -> {
            String path = codeGalaxy.getFilePath().toString();
            int startPosition = codeGalaxy.getSelection().getStart();
            int line = codeGalaxy.offsetToPosition(startPosition, TwoDimensional.Bias.Forward).getMajor();     // line number (paragraph index)
            int column = codeGalaxy.offsetToPosition(startPosition, TwoDimensional.Bias.Forward).getMinor();

            renamePopup.getTextField().setText(codeGalaxy.getSelectedText());

            renamePopup.show(Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage());

            renamePopup.getTextField().setOnAction(_ -> {
                String newName = renamePopup.getTextField().getText();
                try {
                    LSP.instance().rename(path, line, column, newName);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                renamePopup.hide();
            });
        });
        return rename;
    }
}
