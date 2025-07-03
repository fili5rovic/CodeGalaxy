package fili5rovic.codegalaxy.code;

import fili5rovic.codegalaxy.code.manager.codeActions.rightClick.CodeRightClickManager;
import fili5rovic.codegalaxy.code.manager.file.FileManager;
import fili5rovic.codegalaxy.code.manager.font.FontManager;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.code.manager.editing.LineEditing;
import fili5rovic.codegalaxy.code.manager.format.FormatManager;
import fili5rovic.codegalaxy.code.manager.highlighting.Highlighter;
import fili5rovic.codegalaxy.code.manager.hover.HoverManager;
import fili5rovic.codegalaxy.code.manager.lsp.LSPManager;
import fili5rovic.codegalaxy.code.manager.suggestions.SuggestionManager;
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

    private Highlighter highlighter;

    private FormatManager formatManager;

    private VirtualizedScrollPane<CodeArea> scrollPane;

    public CodeGalaxy() {
        addLineNumbers();
        addManagers();
        initManagers();
    }


    public void setFile(Path path) {
        fileManager = new FileManager(this, path);
        fileManager.init();
    }

    public void save() {
        if (fileManager != null)
            fileManager.save();
    }

    public void format() {
        if (formatManager != null)
            formatManager.formatDocument();
    }

    private void addLineNumbers() {
        setParagraphGraphicFactory(LineNumberFactory.get(this));
        this.scrollPane = new VirtualizedScrollPane<>(this);
    }

    private void addManagers() {
        managers.add(new FontManager(this));
        managers.add(new LineEditing(this));
        managers.add(new SuggestionManager(this));
        formatManager = new FormatManager(this);
        managers.add(formatManager);
        highlighter = new Highlighter(this);
        managers.add(highlighter);
        managers.add(new LSPManager(this));
        managers.add(new CodeRightClickManager(this));
//        managers.add(new HoverManager(this)); TODO uncomment
    }

    private void initManagers() {
        for (Manager m : managers)
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

    public Highlighter getHighlighter() {
        return highlighter;
    }

    public Path getFilePath() {
        if (fileManager != null)
            return fileManager.getPath();
        return null;
    }

    public VirtualizedScrollPane<CodeArea> getScrollPane() {
        return scrollPane;
    }

    public void selectWordAtCaret() {
        int caretPosition = getCaretPosition();
        int paragraphIndex = getCurrentParagraph();
        String paragraphText = getParagraph(paragraphIndex).getText();

        if (paragraphText.isEmpty())
            return;

        int localCaret = caretPosition - getAbsolutePosition(paragraphIndex, 0);
        if (localCaret < 0 || localCaret >= paragraphText.length())
            localCaret = Math.max(0, Math.min(paragraphText.length() - 1, localCaret));

        if (localCaret == paragraphText.length() || !Character.isLetterOrDigit(paragraphText.charAt(localCaret))) {
            int temp = localCaret - 1;
            while (temp >= 0 && !Character.isLetterOrDigit(paragraphText.charAt(temp))) {
                temp--;
            }
            localCaret = temp;
        }

        if (localCaret < 0 || localCaret >= paragraphText.length())
            return;

        int wordStart = localCaret;
        int wordEnd = localCaret;

        while (wordStart > 0 && Character.isLetterOrDigit(paragraphText.charAt(wordStart - 1))) {
            wordStart--;
        }
        while (wordEnd < paragraphText.length() - 1 && Character.isLetterOrDigit(paragraphText.charAt(wordEnd + 1))) {
            wordEnd++;
        }

        int absStart = getAbsolutePosition(paragraphIndex, wordStart);
        int absEnd = getAbsolutePosition(paragraphIndex, wordEnd + 1); // exclusive

        selectRange(absStart, absEnd);
    }

}
