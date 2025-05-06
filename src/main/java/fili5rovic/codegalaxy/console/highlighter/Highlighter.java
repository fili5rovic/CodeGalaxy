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
            consoleArea.setStyleSpans(0, computeHighlighting(newValue));
        });
    }


    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        spansBuilder.add(Collections.singleton("default_text"), text.length());
        return spansBuilder.create();
    }
}
