package fili5rovic.codegalaxy.codeRunner;

import fili5rovic.codegalaxy.util.MetaDataHelper;

import java.io.*;
import java.nio.file.Path;

public class CodeRunner {

    public static void runJava(Path javaFilePath) throws Exception {
        if(!javaFilePath.toString().endsWith(".java")) {
            System.err.println("Not a java file");
            return;
        }
        Path filePath = filePathToClassPath(javaFilePath);
        if (filePath == null) {
            System.err.println("File path is null.");
            return;
        }

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


}
