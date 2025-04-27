package fili5rovic.codegalaxy.code.manager.font;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.settings.ProjectSettings;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.util.Debouncer;
import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;
import org.fxmisc.richtext.LineNumberFactory;

import java.util.function.IntFunction;

public class FontManager extends Manager {
    private static final int MIN_FONT_SIZE;
    private static final int MAX_FONT_SIZE;
    private static final int DEFAULT_FONT_SIZE;
    private int currentFontSize;

    private FontPopUpManager fontPopUpManager;

    private Debouncer debouncer = new Debouncer();

    static {
        MIN_FONT_SIZE = 10;
        MAX_FONT_SIZE = 50;
        DEFAULT_FONT_SIZE = 24;
    }

    public FontManager(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    public void init() {
        fontPopUpManager = new FontPopUpManager(codeGalaxy);
        fontPopUpManager.init();

        String fontSizeStr = ProjectSettings.getInstance().get("fontSize");
        if (fontSizeStr == null) {
            currentFontSize = DEFAULT_FONT_SIZE;
            ProjectSettings.getInstance().set("fontSize", String.valueOf(currentFontSize));
        } else {
            currentFontSize = Integer.parseInt(fontSizeStr);
        }

        updateUI();
        setupListener();
    }

    private void setupListener() {
        codeGalaxy.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                if (event.getDeltaY() > 0)
                    increaseSize();
                else
                    decreaseSize();
                updateUI();
                popUp();
                debouncer.debounce(() -> ProjectSettings.getInstance().set("fontSize", String.valueOf(currentFontSize)),5000);
                event.consume();
            }
        });
    }

    private void increaseSize() {
        if (currentFontSize < MAX_FONT_SIZE)
            currentFontSize++;
    }

    private void decreaseSize() {
        if (currentFontSize > MIN_FONT_SIZE)
            currentFontSize--;
    }

    private void updateUI() {
        codeGalaxy.setStyle("-fx-font-size: " + currentFontSize);


        IntFunction<Node> lineNumberFactory = LineNumberFactory.get(codeGalaxy);
        codeGalaxy.setParagraphGraphicFactory(line -> {
            Node lineNumber = lineNumberFactory.apply(line);
            lineNumber.setStyle("-fx-font-size: " + currentFontSize + "px;");
            return lineNumber;
        });

    }

    public void popUp() {
        fontPopUpManager.showMessage(currentFontSize + "pt");
    }

    public static int getMaxFontSize() {
        return MAX_FONT_SIZE;
    }

    public static int getMinFontSize() {
        return MIN_FONT_SIZE;
    }


}
