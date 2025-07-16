package fili5rovic.codegalaxy.console.highlighter;

import fili5rovic.codegalaxy.Main;
import fili5rovic.codegalaxy.console.ConsoleArea;
import fili5rovic.codegalaxy.util.CSSUtil;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Highlighter {

    public static void apply(ConsoleArea consoleArea) {
        CSSUtil.applyStylesheet(consoleArea.getStylesheets(), "highlighter");
        listener(consoleArea);
    }

    private static void listener(ConsoleArea consoleArea) {
        consoleArea.textProperty().addListener((_, oldValue, newValue) -> {
            int size = newValue.length() - oldValue.length();
            if (size <= 0) return;
            String style = consoleArea.getStyleClassForTextType();
            consoleArea.setStyleSpans(oldValue.length(), computeHighlighting(size, style));
        });
    }


    public static StyleSpans<Collection<String>> computeHighlighting(int length, String style) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        spansBuilder.add(List.of(style, "code-font"), length);
        return spansBuilder.create();
    }


}
