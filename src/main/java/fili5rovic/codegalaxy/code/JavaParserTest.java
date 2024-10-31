package fili5rovic.codegalaxy.code;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.FileNotFoundException;

public class JavaParserTest {


    public static void main(String[] args) {
        String fileStr = "D:\\PROJECTS\\JavaCustomProjects\\CodeGalaxy\\model\\Gay.java";
        JavaParser j = new JavaParser();
        try {
            ParseResult<CompilationUnit> parse = j.parse(new File(fileStr));
            if (parse.getResult().isPresent()) {
                CompilationUnit cu = parse.getResult().get();
                cu.findAll(com.github.javaparser.ast.body.MethodDeclaration.class).forEach(method -> System.out.println(method.getNameAsString()));

            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
