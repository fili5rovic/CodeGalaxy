package fili5rovic.codegalaxy.codeRunner;

import fili5rovic.codegalaxy.util.MetaDataHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodeRunnerJava {

    static Process runJava(Path javaFilePath, String[] vmOptions, String[] programArgs) throws Exception {
        if (!javaFilePath.toString().endsWith(".java"))
            throw new IllegalArgumentException("File is not a Java file");

        String qualifiedClassName = getQualifiedClassName(javaFilePath);
        return runJava(qualifiedClassName, vmOptions, programArgs);
    }

    static Process runJava(String qualifiedName, String[] vmOptions, String[] programArgs) throws IOException {
        String classPath = MetaDataHelper.getOutputPath();
        if (classPath == null)
            throw new IllegalArgumentException("Classpath is null");

        System.out.println("Args: " + Arrays.toString(programArgs));

        List<String> command = createCommand(qualifiedName, classPath, vmOptions, programArgs);

        ProcessBuilder pb = new ProcessBuilder(command);

        return pb.start();
    }

    private static List<String> createCommand(String fileName, String classPath, String[] vmOptions, String[] programArgs) {
        List<String> command = new ArrayList<>();
        command.add("java");

        if (vmOptions != null) {
            command.addAll(List.of(vmOptions));
        }

        command.add("-cp");
        command.add(classPath);

        command.add(fileName);

        if (programArgs != null) {
            command.addAll(List.of(programArgs));
        }
        // java [<vm-options>] -cp <classpath> <main-class> [<program-args>...]
        return command;
    }

    public static String getQualifiedClassName(Path javaFilePath) {
        String sourcePath = MetaDataHelper.getSrcPath();
        if (sourcePath == null) {
            System.err.println("Source path not set in project.");
            return null;
        }

        Path relativePath = Path.of(sourcePath).relativize(javaFilePath);

        return relativePath.toString()
                .replace(".java", "")
                .replace("/", ".")
                .replace("\\", ".");
    }

}
