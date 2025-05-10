package fili5rovic.codegalaxy.code.manager.codeActions.rightClick;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.lsp.LSP;
import fili5rovic.codegalaxy.util.SVGUtil;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class CodeRightClickManager extends Manager {

    private final ContextMenu contextMenu = new ContextMenu();


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
    }

    private void updateContextMenuItems() {
        contextMenu.getItems().clear();

        MenuItem copy = new MenuItem("Copy");
        copy.setGraphic(SVGUtil.getUI("copy",16,16));
        copy.setOnAction(e -> {
            codeGalaxy.copy();
        });
        contextMenu.getItems().add(copy);

        MenuItem paste = new MenuItem("Paste");
        paste.setGraphic(SVGUtil.getUI("paste",16,16));
        paste.setOnAction(e -> {
            codeGalaxy.paste();
        });
        contextMenu.getItems().add(paste);

        MenuItem cut = new MenuItem("Cut");
        cut.setGraphic(SVGUtil.getUI("cut",16,16));
        cut.setOnAction(e -> {
            codeGalaxy.cut();
        });
        contextMenu.getItems().add(cut);

        Menu refactor = new Menu("Refactor");
        refactor.getItems().add(createRenameMenuItem());

        contextMenu.getItems().add(refactor);

    }

    private MenuItem createRenameMenuItem() {
        MenuItem rename = new MenuItem("Rename");
        rename.setOnAction(e -> {
            String path = codeGalaxy.getFilePath().toString();
            int startPosition = codeGalaxy.getSelection().getStart();
            int line = codeGalaxy.offsetToPosition(startPosition,  org.fxmisc.richtext.model.TwoDimensional.Bias.Forward).getMajor();     // line number (paragraph index)
            int column = codeGalaxy.offsetToPosition(startPosition,  org.fxmisc.richtext.model.TwoDimensional.Bias.Forward).getMinor();
            try {
                LSP.instance().rename(path, line, column, codeGalaxy.getSelectedText() + "1");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return rename;
    }
}
