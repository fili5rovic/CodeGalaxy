package fili5rovic.codegalaxy.console.highlighter;

import fili5rovic.codegalaxy.Main;
import fili5rovic.codegalaxy.console.ConsoleArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class Highlighter {

    public static void apply(ConsoleArea consoleArea) {
        consoleArea.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/highlighter.css")).toExternalForm());
        listener(consoleArea);
    }

    private static void listener(ConsoleArea consoleArea) {
        consoleArea.textProperty().addListener((_, _, newValue) -> {
            System.out.println("Text changed: " + newValue);
            consoleArea.setStyleSpans(0, computeHighlighting(newValue, consoleArea.getInputStart(), consoleArea.getInputEnd()));
        });
    }


    public static StyleSpans<Collection<String>> computeHighlighting(String text, int inputStart, int inputEnd) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        if (inputStart > 0)
            spansBuilder.add(Collections.singleton("default_text"), inputStart);

        if (inputEnd > inputStart)
            spansBuilder.add(Collections.singleton("prompt"), inputEnd - inputStart);

        if (inputEnd < text.length())
            spansBuilder.add(Collections.singleton("default_text"), text.length() - inputEnd);

        return spansBuilder.create();
    }

}
