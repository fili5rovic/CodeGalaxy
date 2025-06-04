package fili5rovic.codegalaxy.code.manager.highlighting;

import fili5rovic.codegalaxy.Main;
import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.util.FileHelper;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Highlighter extends Manager {

    private final HashMap<String, String> fileNameToStyleClassMap = new HashMap<>();

    private final HashMap<String, ArrayList<Range>> symbolRanges = new HashMap<>();

    public Highlighter(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    public void init() {
        codeGalaxy.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/highlighter.css")).toExternalForm());
        fillHashMap();
    }

    private void fillHashMap() {
        fileNameToStyleClassMap.put("javaKeywords.txt", "keywords");
    }

    public void applyHighlighting(CodeArea codeArea) {
        String text = codeArea.getText();
        if (text.isEmpty())
            return;

        codeArea.setStyleSpans(0, computeHighlighting(text));
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        int lastIndex = 0;


        List<Range> keywordRanges = getKeywords(text);
        List<Range> stringLiteralRanges = getStringLiterals(text);

        List<StyledRange> allRanges = new ArrayList<>();

        for (String key : symbolRanges.keySet()) {
            ArrayList<Range> ranges = symbolRanges.get(key);
            for (Range r : ranges) {
                allRanges.add(new StyledRange(r.start(), r.end(), key));
            }
        }

        for (Range r : keywordRanges)
            allRanges.add(new StyledRange(r.start(), r.end(), "keyword"));

        for (Range r : stringLiteralRanges)
            allRanges.add(new StyledRange(r.start(), r.end(), "string"));

        for (Range r : getComments(text))
            allRanges.add(new StyledRange(r.start(), r.end(), "comment"));

        allRanges.sort(Comparator.comparingInt(r -> r.start));

        for (StyledRange r : allRanges) {
            if (r.start > lastIndex) {
                spansBuilder.add(List.of("default_text", "code-font"), r.start - lastIndex);
            }
            spansBuilder.add(List.of(r.styleClass, "code-font"), r.end - r.start);
            lastIndex = r.end;
        }

        if (lastIndex < text.length()) {
            spansBuilder.add(List.of("default_text", "code-font"), text.length() - lastIndex);
        }

        return spansBuilder.create();
    }

    record StyledRange(int start, int end, String styleClass) {
    }


    private List<Range> getKeywords(String code) {
        String patterns = "";
        for (String key : fileNameToStyleClassMap.keySet()) {
            String path = Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/keywords/" + key)).getPath();
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

    private List<Range> getStringLiterals(String code) {
        List<Range> ranges = new ArrayList<>();

        Pattern stringPattern = Pattern.compile("\"([^\"\\\\]|\\\\.)*\"");
        Matcher matcher = stringPattern.matcher(code);

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            ranges.add(new Range(start, end));
        }

        return ranges;
    }

    private List<Range> getComments(String code) {
        List<Range> ranges = new ArrayList<>();

        Pattern commentPattern = Pattern.compile("//.*|/\\*(.|\\R)*?\\*/");
        Matcher matcher = commentPattern.matcher(code);

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            ranges.add(new Range(start, end));
        }

        return ranges;
    }


    public void setSymbolRanges(HashMap<String, ArrayList<Range>> symbolRanges) {
        this.symbolRanges.clear();
        this.symbolRanges.putAll(symbolRanges);
    }


}

