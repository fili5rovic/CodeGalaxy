package fili5rovic.codegalaxy.code.manager.highlighting;

import fili5rovic.codegalaxy.Main;
import fili5rovic.codegalaxy.util.FileHelper;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Highlighter {

    private static final HashMap<String,String> fileNameToStyleClassMap = new HashMap<>();

    public static void init(CodeArea codeArea) {
        codeArea.getStylesheets().add(Main.class.getResource("/fili5rovic/codegalaxy/highlighter.css").toExternalForm());
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        fillHashMap();
        codeArea.textProperty().addListener((obs, oldText, newText) -> Highlighter.applyHighlighting(codeArea));
    }

    private static void fillHashMap() {
        fileNameToStyleClassMap.put("javaKeywords.txt", "keywords");
    }

    private static void applyHighlighting(CodeArea codeArea) {
        String text = codeArea.getText();
        codeArea.setStyleSpans(0, computeHighlighting(text));
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        String[] patterns = createPatternsFromHashMap();
        String[] cssClasses = {
                "keyword"
        };

        String combinedPattern = String.join("|", patterns);
        Pattern pattern = Pattern.compile(combinedPattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        int lastIndex = 0;
        while (matcher.find()) {
            String styleClass = null;
            for (int i = 0; i < patterns.length; i++) {
                if (matcher.group().matches(patterns[i])) {
                    styleClass = cssClasses[i];
                    break;
                }
            }
            spansBuilder.add(Collections.singleton("default_text"), matcher.start() - lastIndex);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastIndex = matcher.end();
        }
        spansBuilder.add(Collections.singleton("default_text"), text.length() - lastIndex);
        return spansBuilder.create();
    }

    private static String[] createPatternsFromHashMap() {
        String[] patterns = new String[fileNameToStyleClassMap.size()];
        int i = 0;

        for(String key : fileNameToStyleClassMap.keySet()) {
            String path = Main.class.getResource("/fili5rovic/codegalaxy/keywords/" + key).getPath();
            String keywords = FileHelper.readFromFile(path);

            StringBuilder sb = new StringBuilder();
            for(String keyword : keywords.split(",")) {
                sb.append("\\b").append(keyword).append("\\b|");
            }
            patterns[i] = sb.substring(0, sb.length() - 1);
            i++;
        }
        return patterns;
    }

}

