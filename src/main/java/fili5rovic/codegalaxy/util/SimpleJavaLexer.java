package fili5rovic.codegalaxy.util;

import java.util.*;
import java.util.regex.*;

public class SimpleJavaLexer {

    public static List<String> getLocalClassNames(String code) {
        Pattern classPattern = Pattern.compile("\\bclass\\s+(\\w+)");
        Matcher matcher = classPattern.matcher(code);

        List<String> classes = new ArrayList<>();
        while (matcher.find()) {
            classes.add(matcher.group(1));
        }
        return classes;
    }

    public static List<String> getMethodNames(String code) {
        String[] classNames = getLocalClassNames(code).toArray(new String[0]);

        Pattern methodPattern = Pattern.compile("(?:public|protected|private|static|\\s)*(?!class\\b)[\\w<>\\[\\]]+\\s+(?<!new\\s)(?<!class\\s)(\\w+)\\s*\\(");
        Matcher matcher = methodPattern.matcher(code);

        List<String> methods = new ArrayList<>();
        while (matcher.find()) {
            String methodName = matcher.group(1);
            boolean isConstructor = false;
            for (String className : classNames) {
                if (methodName.equals(className)) {
                    isConstructor = true;
                    break;
                }
            }

            // only add if it's not a constructor
            if (!isConstructor) {
                methods.add(methodName);
            }
        }
        return methods;
    }

    public static List<String> getFieldNames(String code) {
        String codeWithoutPackages = code.replaceAll("(?m)^\\s*(package|import)\\s+.*?;\\s*$", "");

        List<String> methodBodies = extractMethodBodies(codeWithoutPackages);
        String codeWithoutMethodContents = codeWithoutPackages;

        for (String methodBody : methodBodies) {
            codeWithoutMethodContents = codeWithoutMethodContents.replace(methodBody, " ".repeat(methodBody.length()));
        }

        Pattern fieldWithModifierPattern = Pattern.compile(
                "(?:public|protected|private|static|final)\\s+[\\w<>\\[\\]]+\\s+(\\w+)\\s*(?:=|;)"
        );
        Matcher modifierMatcher = fieldWithModifierPattern.matcher(codeWithoutMethodContents);

        Pattern defaultFieldPattern = Pattern.compile(
                "(?m)^\\s*(?!(public|protected|private|static|final|class|void|if|for|while|return|else)\\b)[\\w<>\\[\\]]+\\s+(\\w+)\\s*(?:=|;)"
        );
        Matcher defaultMatcher = defaultFieldPattern.matcher(codeWithoutMethodContents);

        List<String> fields = new ArrayList<>();
        while (modifierMatcher.find()) {
            fields.add(modifierMatcher.group(1));
        }

        while (defaultMatcher.find()) {
            fields.add(defaultMatcher.group(2));
        }

        return fields;
    }


    private static List<String> extractMethodBodies(String code) {
        List<String> methodBodies = new ArrayList<>();
        Pattern methodBodyPattern = Pattern.compile("(?:public|protected|private|static|\\s)*[\\w<>\\[\\]]+\\s+\\w+\\s*\\([^)]*\\)\\s*\\{([^{}]*(\\{[^{}]*\\}[^{}]*)*)*\\}");
        Matcher methodBodyMatcher = methodBodyPattern.matcher(code);

        while (methodBodyMatcher.find()) {
            String fullMethod = methodBodyMatcher.group();
            int openBraceIndex = fullMethod.indexOf('{');
            if (openBraceIndex >= 0) {
                methodBodies.add(fullMethod.substring(openBraceIndex));
            }
        }

        return methodBodies;
    }

    public static List<String> getLocalVariableNames(String code) {
        List<String> variables = new ArrayList<>();
        List<String> methodBodies = extractMethodBodies(code);

        for (String methodBody : methodBodies) {
            Pattern localVarPattern = Pattern.compile("\\b(?!return\\b)[\\w<>\\[\\]]+\\s+(\\w+)\\s*(?:=|;)");
            Matcher localVarMatcher = localVarPattern.matcher(methodBody);

            while (localVarMatcher.find()) {
                variables.add(localVarMatcher.group(1));
            }
        }

        return variables;
    }

    public static void main(String[] args) {
        String code = """
                    package test;
                
                     public class Gay {
                     	int x = 3;
                
                     	public Gay() {
                
                     	}
                     	private int gay = 4;
                     	
                     	private void gay() {
                     	
                     	}
                     }
                """;

        System.out.println("Classes: " + getLocalClassNames(code));
        System.out.println("Methods: " + getMethodNames(code));
        System.out.println("Fields: " + getFieldNames(code));
        System.out.println("Local Variables: " + getLocalVariableNames(code));
    }
}