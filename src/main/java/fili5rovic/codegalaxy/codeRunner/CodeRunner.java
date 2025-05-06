package fili5rovic.codegalaxy.codeRunner;

import fili5rovic.codegalaxy.util.MetaDataHelper;

import java.nio.file.Path;

public class CodeRunner {

    static Process runJava(Path javaFilePath) throws Exception {
        if (!javaFilePath.toString().endsWith(".java"))
            throw new IllegalArgumentException("File is not a Java file");

        Path filePath = filePathToClassPath(javaFilePath);
        if (filePath == null)
            throw new IllegalArgumentException("File path is null");


        String fileName = filePath.getFileName().toString();
        String classPath = filePath.getParent().toString();

        ProcessBuilder pb = new ProcessBuilder(
                "java", "-cp", classPath, fileName
        );

        return pb.start();
    }

    private static Path filePathToClassPath(Path filePath) {
        String classPath = MetaDataHelper.getClasspathPath("output");
        String sourcePath = MetaDataHelper.getClasspathPath("src");
        if (classPath == null) {
            System.err.println("Class path not set in project.");
            return null;
        }
        if (sourcePath == null) {
            System.err.println("Source path not set in project.");
            return null;
        }
        String relativePath = Path.of(sourcePath).relativize(filePath).toString();
        return Path.of(classPath).resolve(relativePath.replace(".java", ""));

    }

}
