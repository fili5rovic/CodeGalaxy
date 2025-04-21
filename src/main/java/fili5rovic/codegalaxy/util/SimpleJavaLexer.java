package fili5rovic.codegalaxy.util;

import fili5rovic.codegalaxy.code.manager.highlighting.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleJavaLexer {

    public static List<Range> getMethodNameRanges(String code) {
        List<Range> ranges = new ArrayList<>();

        Pattern pattern = Pattern.compile(
                "(?:public|protected|private|static|final|synchronized)?\\s*" +
                        "(?:[\\w<>\\[\\]]+\\s+)+" +
                        "(\\w+)\\s*\\(");
        Matcher matcher = pattern.matcher(code);

        while (matcher.find()) {
            int start = matcher.start(1);
            int end = matcher.end(1);
            ranges.add(new Range(start, end));
        }

        return ranges;
    }

    public static List<Range> getFieldRanges(String code) {
        List<Range> ranges = new ArrayList<>();

        // Match fields: [modifier] type name ;
        Pattern pattern = Pattern.compile(
                "(?:public|protected|private|static|final)?\\s*" +
                        "(?:[\\w<>\\[\\]]+)\\s+" +
                        "(\\w+)\\s*(?:=|;)");
        Matcher matcher = pattern.matcher(code);

        while (matcher.find()) {
            int start = matcher.start(1);
            int end = matcher.end(1);
            ranges.add(new Range(start, end));
        }

        return ranges;
    }

    public static List<Range> getClassNameRanges(String code) {
        List<Range> ranges = new ArrayList<>();

        Pattern pattern = Pattern.compile(
                "\\bclass\\s+(\\w+)\\s*\\{");
        Matcher matcher = pattern.matcher(code);

        while (matcher.find()) {
            int start = matcher.start(1);
            int end = matcher.end(1);
            ranges.add(new Range(start, end));
        }

        return ranges;
    }

}
