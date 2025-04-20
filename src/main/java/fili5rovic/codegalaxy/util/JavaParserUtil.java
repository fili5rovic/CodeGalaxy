package fili5rovic.codegalaxy.util;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;

import java.io.File;
import java.io.IOException;

public class JavaParserUtil {

    public static String[] getMethodsFromFile(File javaFile) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(javaFile);
            return cu.findAll(MethodDeclaration.class).stream()
                    .map(MethodDeclaration::getNameAsString)
                    .toArray(String[]::new);
        } catch (IOException e) {
            System.err.println("Error parsing file: " + e.getMessage());
            return new String[0];
        }
    }

    public static String[] getClassesFromFile(File javaFile) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(javaFile);
            return cu.findAll(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class).stream()
                    .map(NodeWithSimpleName::getNameAsString)
                    .toArray(String[]::new);
        } catch (IOException e) {
            System.err.println("Error parsing file: " + e.getMessage());
            return new String[0];
        }
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
}
