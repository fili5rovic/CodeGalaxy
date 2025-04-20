package fili5rovic.codegalaxy.codeRunner;

import fili5rovic.codegalaxy.util.MetaDataHelper;
import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;

import java.io.*;
import java.nio.file.Path;

public class CodeRunner {

    public static void main(String[] args) {
        try {
            runJava(Path.of("D:\\MY_WORKSPACE\\Sex\\src\\Main.java"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void runJava(Path javaFilePath) throws Exception {
        if(!javaFilePath.endsWith(".java")) {
            System.err.println("Not a java file");
            return;
        }
        Path filePath = filePathToClassPath(javaFilePath);
        if (filePath == null) {
            System.err.println("File path is null.");
            return;
        }

        // get filename and path without filename
        String fileName = filePath.getFileName().toString();
        String classPath = filePath.getParent().toString();

        ProcessBuilder pb = new ProcessBuilder(
                "java", "-cp", classPath, fileName
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        process.waitFor();
    }

    private static Path filePathToClassPath(Path filePath) {
        String classPath = MetaDataHelper.getClasspathPath("output");
        String sourcePath = MetaDataHelper.getClasspathPath("src");
        if (classPath == null) {
            System.err.println("Class path not set in user preferences.");
            return null;
        }
        if (sourcePath == null) {
            System.err.println("Source path not set in user preferences.");
            return null;
        }
        String relativePath = Path.of(sourcePath).relativize(filePath).toString();
        return Path.of(classPath).resolve(relativePath.replace(".java", ""));

    }

    public static boolean hasMainMethodInSource(File javaFile) {
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
