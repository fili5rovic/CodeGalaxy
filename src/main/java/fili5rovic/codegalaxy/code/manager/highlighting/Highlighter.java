package fili5rovic.codegalaxy.code.manager.highlighting;

import fili5rovic.codegalaxy.Main;
import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.util.FileHelper;
import fili5rovic.codegalaxy.util.SimpleJavaLexer;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Highlighter extends Manager {

    private final HashMap<String, String> fileNameToStyleClassMap = new HashMap<>();

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
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        int lastIndex = 0;

        List<Range> methodRanges = SimpleJavaLexer.getMethodNameRanges(text);
        List<Range> fieldRanges = SimpleJavaLexer.getFieldRanges(text);
        List<Range> classRanges = SimpleJavaLexer.getClassNameRanges(text);
        List<Range> keywordRanges = getKeywords(text);

        List<StyledRange> allRanges = new ArrayList<>();

        for(Range r : keywordRanges)
            allRanges.add(new StyledRange(r.start(), r.end(), "keyword"));

        for (Range r : methodRanges)
            allRanges.add(new StyledRange(r.start(), r.end(), "method"));

        for (Range r : fieldRanges)
            allRanges.add(new StyledRange(r.start(), r.end(), "field"));

        for (Range r : classRanges)
            allRanges.add(new StyledRange(r.start(), r.end(), "class"));


        // Sort ranges by start position
        allRanges.sort(Comparator.comparingInt(r -> r.start));

        for (StyledRange r : allRanges) {
            if (r.start > lastIndex) {
                spansBuilder.add(Collections.singleton("default_text"), r.start - lastIndex);
            }
            spansBuilder.add(Collections.singleton(r.styleClass), r.end - r.start);
            lastIndex = r.end;
        }

        if (lastIndex < text.length()) {
            spansBuilder.add(Collections.singleton("default_text"), text.length() - lastIndex);
        }

        return spansBuilder.create();
    }

    record StyledRange(int start, int end, String styleClass) {
    }


    private List<Range> getKeywords(String code) {
        String patterns = "";
        for (String key : fileNameToStyleClassMap.keySet()) {
            String path = Main.class.getResource("/fili5rovic/codegalaxy/keywords/" + key).getPath();
            String keywords = FileHelper.readFromFile(path);

            StringBuilder sb = new StringBuilder();
            for (String keyword : keywords.split(",")) {
                sb.append("\\b").append(keyword).append("\\b|");
            }
            patterns = sb.substring(0, sb.length() - 1);
        }

        // get ranges of keywords in code
        List<Range> ranges = new ArrayList<>();
        Pattern pattern = Pattern.compile(patterns);
        Matcher matcher = pattern.matcher(code);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            ranges.add(new Range(start, end));
        }

        return ranges;
    }


}

