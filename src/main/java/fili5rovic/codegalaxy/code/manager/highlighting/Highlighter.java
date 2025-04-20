package fili5rovic.codegalaxy.code.manager.highlighting;

import fili5rovic.codegalaxy.Main;
import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.util.FileHelper;
import fili5rovic.codegalaxy.util.JavaParserUtil;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Highlighter extends Manager {

    private final HashMap<String,String> fileNameToStyleClassMap = new HashMap<>();

    public Highlighter(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    public void init() {
        codeGalaxy.getStylesheets().add(Main.class.getResource("/fili5rovic/codegalaxy/highlighter.css").toExternalForm());
        codeGalaxy.setParagraphGraphicFactory(LineNumberFactory.get(codeGalaxy));
        fillHashMap();
        codeGalaxy.textProperty().addListener((obs, oldText, newText) -> this.applyHighlighting(codeGalaxy));
    }


    private void fillHashMap() {
        fileNameToStyleClassMap.put("javaKeywords.txt", "keywords");
    }

    private void applyHighlighting(CodeArea codeArea) {
        String text = codeArea.getText();
        codeArea.setStyleSpans(0, computeHighlighting(text));
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        String[] patterns = {
                getKeywords(),
                getMethods(),
                getClasses()
        };

        String[] cssClasses = {
                "keyword",
                "method",
                "class",
        };

        String combinedPattern = String.join("|", patterns);
        Pattern pattern = Pattern.compile(combinedPattern);
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

    private String getKeywords() {
        String patterns = "";
        for(String key : fileNameToStyleClassMap.keySet()) {
            String path = Main.class.getResource("/fili5rovic/codegalaxy/keywords/" + key).getPath();
            String keywords = FileHelper.readFromFile(path);

            StringBuilder sb = new StringBuilder();
            for(String keyword : keywords.split(",")) {
                sb.append("\\b").append(keyword).append("\\b|");
            }
            patterns = sb.substring(0, sb.length() - 1);
        }
        return patterns;
    }

    private String getMethods() {
        StringBuilder sb = new StringBuilder();
        for(String method : JavaParserUtil.getMethodsFromFile(codeGalaxy.getFilePath().toFile())) {
            sb.append("\\b").append(method).append("\\b|");
        }
        return sb.substring(0, sb.length() - 1);
    }

    private String getClasses() {
        StringBuilder sb = new StringBuilder();
        for(String className : JavaParserUtil.getClassesFromFile(codeGalaxy.getFilePath().toFile())) {
            sb.append("\\b").append(className).append("\\b|");
        }
        return sb.substring(0, sb.length() - 1);
    }

}

