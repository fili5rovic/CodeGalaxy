package fili5rovic.codegalaxy.code;

import fili5rovic.codegalaxy.code.manager.FontManager;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.code.manager.editing.LineEditing;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.util.ArrayList;


public class CodeGalaxy extends CodeArea {
    private final ArrayList<Manager> managers = new ArrayList<>();

    public CodeGalaxy() {
        addLineNumbers();
        addManagers();
        initManagers();

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
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }

    public boolean hasSelection() {
        return !this.getSelectedText().isEmpty();
    }
    public int getParagraphsCount() {
        return ((ObservableList<?>) this.getParagraphs()).size();
    }

}
