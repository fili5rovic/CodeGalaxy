package fili5rovic.codegalaxy.codeRunner;

import fili5rovic.codegalaxy.util.MetaDataHelper;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CodeRunnerJava {

    static Process runJava(Path javaFilePath, String[] vmOptions, String[] programArgs) throws Exception {
        if (!javaFilePath.toString().endsWith(".java"))
            throw new IllegalArgumentException("File is not a Java file");
        String classPath = getBuildDir(javaFilePath);
        if (classPath == null)
            throw new IllegalArgumentException("File path is null");

        String fileName = getQualifiedClassName(javaFilePath);

        System.out.println("Running Java file: " + classPath);
        System.out.println("Main class name: " + fileName);
        List<String> command = createCommand(fileName, classPath, vmOptions, programArgs);

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

    private static String getBuildDir(Path filePath) {
        String classPath = MetaDataHelper.getOutputPath();
        if (classPath == null) {
            System.err.println("Class path not set in project.");
            return null;
        }
        return classPath;
    }

    public static String getQualifiedClassName(Path javaFilePath) {
        String sourcePath = MetaDataHelper.getSrcPath();
        if (sourcePath == null) {
            System.err.println("Source path not set in project.");
            return null;
        }

        Path relativePath = Path.of(sourcePath).relativize(javaFilePath);
        String className = relativePath.toString()
                .replace(".java", "")
                .replace("/", ".")
                .replace("\\", ".");

        return className;
    }

}
