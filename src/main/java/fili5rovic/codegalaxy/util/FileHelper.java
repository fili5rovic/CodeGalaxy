package fili5rovic.codegalaxy.util;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FileHelper {

    public static String readFromFile(String path) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
            if (!sb.isEmpty())
                sb.deleteCharAt(sb.length() - 1);
        } catch (IOException e) {
            System.out.println("Couldn't read file " + path);
        }
        return sb.toString();
    }

    public static void writeToFile(String path, String content) {
        try {
            Files.write(Path.of(path), content.getBytes());
        } catch (IOException e) {
            System.out.println("Couldn't write to file " + path);
        }
    }

    public static void deleteRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteRecursively(entry);
                }
            }
        }
        Files.delete(path);
    }

    public static File openFolderChooser(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");
        return directoryChooser.showDialog(stage);
    }

    public static void openDirectoryInExplorer(File directory) throws IOException {
        if (!directory.exists()) {
            throw new IOException("Directory does not exist: " + directory.getAbsolutePath());
        }

        String osName = System.getProperty("os.name").toLowerCase();

        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.OPEN)) {
                desktop.open(directory);
                return;
            }
        }

        ProcessBuilder builder;

        if (osName.contains("win")) {
            builder = new ProcessBuilder("explorer.exe", directory.getAbsolutePath());
        } else if (osName.contains("mac") || osName.contains("darwin")) {
            builder = new ProcessBuilder("open", directory.getAbsolutePath());
        } else if (osName.contains("nux") || osName.contains("nix")) {
            builder = new ProcessBuilder("xdg-open", directory.getAbsolutePath());
        } else {
            throw new IOException("Unsupported operating system: " + osName);
        }

        builder.start();
    }

    public static Path[] getAllFilesInDirectory(Path directory) throws IOException {
        try (Stream<Path> stream = Files.walk(directory)) {
            return stream.filter(Files::isRegularFile).toArray(Path[]::new);
        }
    }


}
