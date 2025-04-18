package fili5rovic.codegalaxy.code;

import fili5rovic.codegalaxy.code.manager.file.FileManager;
import fili5rovic.codegalaxy.code.manager.font.FontManager;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.code.manager.editing.LineEditing;
import fili5rovic.codegalaxy.code.manager.highlighting.Highlighter;
import fili5rovic.codegalaxy.code.manager.suggestions.SuggestionsHelper;
import fili5rovic.codegalaxy.lsp.LSPManager;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.nio.file.Path;
import java.util.ArrayList;

public class CodeGalaxy extends CodeArea {
    private final ArrayList<Manager> managers = new ArrayList<>();

    private FileManager fileManager;

    public CodeGalaxy() {
        addLineNumbers();
        addManagers();
        initManagers();
        Highlighter.init(this);
        SuggestionsHelper.suggestion(this);
    }

    public void setFile(Path path) {
        fileManager = new FileManager(this, path);
        fileManager.init();
        try {
            LSPManager.getInstance().openFile(path.toString());
        } catch (Exception e) {
            System.out.println("Failed to open file: " + e.getMessage());
        }
    }

    public void save() {
        if(fileManager != null)
            fileManager.save();
    }

    private void addLineNumbers() {
        setParagraphGraphicFactory(LineNumberFactory.get(this));
        VirtualizedScrollPane<CodeArea> vsPane = new VirtualizedScrollPane<>(this);
    }

    private void addManagers() {
        managers.add(new FontManager(this));
        managers.add(new LineEditing(this));

    }

    private void initManagers() {
        for(Manager m : managers)
            m.init();
    }

    @Override
    public void replaceText(int start, int end, String text) {
        super.replaceText(start, end, text);
        onTextChanged();
    }

    private void onTextChanged() {
        try {
            LSPManager.getInstance().sendChange(getFilePath().toString(), getText());
        } catch (Exception e) {
            System.out.println("Failed to send change: " + e.getMessage());
        }
    }

    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }

    public boolean hasSelection() {
        return !this.getSelectedText().isEmpty();
    }
    public int getParagraphsCount() {
        return ((ObservableList<?>) this.getParagraphs()).size();
    }

    public Path getFilePath() {
        if(fileManager != null)
            return fileManager.getPath();
        return null;
    }
}
