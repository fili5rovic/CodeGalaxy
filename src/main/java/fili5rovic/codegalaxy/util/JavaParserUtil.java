package fili5rovic.codegalaxy.util;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class JavaParserUtil {

    static {
        // Disable attribute comments (which cause extra errors sometimes)
        ParserConfiguration config = new ParserConfiguration();
        config.setAttributeComments(false);
        StaticJavaParser.setConfiguration(config);
    }

    public static Optional<CompilationUnit> tryParse(File code) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(code);
            return Optional.of(cu);
        } catch (Exception e) {
            // Optionally log problems if you want:
            System.err.println("Parse problem: " + e.getMessage());
            return Optional.empty();

        }
    }

    public static String[] getMethods(File code) {
        return tryParse(code)
                .map(cu -> cu.findAll(MethodDeclaration.class).stream()
                        .map(MethodDeclaration::getNameAsString)
                        .toArray(String[]::new))
                .orElse(new String[0]);
    }

    public static String[] getMethodVariables(File code) {
        return tryParse(code)
                .map(cu -> {
                    List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
                    if (methods.isEmpty()) return new String[0];

                    return methods.stream()
                            .flatMap(method -> {
                                Stream<String> paramNames = method.getParameters().stream()
                                        .map(param -> param.getNameAsString());

                                Stream<String> localVarNames = method.getBody()
                                        .map(body -> body.findAll(VariableDeclarator.class).stream()
                                                .map(var -> var.getNameAsString()))
                                        .orElse(Stream.empty());

                                return Stream.concat(paramNames, localVarNames);
                            })
                            .toArray(String[]::new);
                })
                .orElse(new String[0]);
    }

    public static boolean hasMainMethod(File javaFile) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(javaFile);

            return cu.findAll(MethodDeclaration.class).stream()
                    .anyMatch(m -> m.getNameAsString().equals("main")
                            && m.isStatic()
                            && m.isPublic()
                            && m.getType().asString().equals("void")
                            && m.getParameters().size() == 1
                            && m.getParameter(0).getType().asString().equals("String[]"));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) throws IOException {
        File file = new File("D:\\MY_WORKSPACE\\Sex\\src\\Main.java");
        String[] variables = getMethodVariables(file);
        for (String variable : variables) {
            System.out.println(variable);
        }
    }
}
